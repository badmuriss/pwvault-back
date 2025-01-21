package com.outis.pwvault.controller;

import com.outis.pwvault.exception.CryptoException;
import com.outis.pwvault.util.CryptoUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;

@RestController
public class UserController {

    private CryptoUtil cryptoUtil;

    @Value("${TENANT_ID}")
    private String tenantId;

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    public UserController(CryptoUtil cryptoUtil){
        this.cryptoUtil = cryptoUtil;
    }

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response) throws IOException, IOException {
        String authorizationUrl = "https://login.microsoftonline.com/" + this.tenantId +
                "/oauth2/v2.0/authorize?client_id=" + this.clientId +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode("http://localhost:8080/callback", "UTF-8") +
                "&response_mode=query" +
                "&scope=https://vault.azure.net/.default" +
                "&state=12345";
        response.sendRedirect(authorizationUrl);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) throws Exception {
        try {
            String url = "https://login.microsoftonline.com/" + this.tenantId + "/oauth2/v2.0/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> inputMap = new LinkedMultiValueMap<>();
            inputMap.add("grant_type", "authorization_code");
            inputMap.add("client_id", this.clientId);
            inputMap.add("client_secret", this.clientSecret);
            inputMap.add("code", code);
            inputMap.add("redirect_uri", "http://localhost:8080/callback");
            inputMap.add("scope", "https://vault.azure.net/.default");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(inputMap, headers);
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> response = template.postForEntity(url, entity, String.class);
            JSONObject myJson = new JSONObject(response.getBody());
            String accessToken = myJson.getString("access_token");

            return cryptoUtil.encryptWithAES(accessToken);
        } catch (Exception e) {
            throw new CryptoException(e.getMessage());
        }
    }

}
