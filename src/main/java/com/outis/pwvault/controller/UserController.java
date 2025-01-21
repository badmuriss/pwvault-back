package com.outis.pwvault.controller;

import com.outis.pwvault.exception.CryptoException;
import com.outis.pwvault.util.CryptoUtil;
import jakarta.servlet.http.HttpServletRequest;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {

    private CryptoUtil cryptoUtil;

    @Value("${TENANT_ID}")
    private String tenantId;

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    @Value("${PWVAULT_FRONT_URI}")
    private String frontendURI;

    public UserController(CryptoUtil cryptoUtil){
        this.cryptoUtil = cryptoUtil;
    }

    @GetMapping("/authorize")
    public ResponseEntity<Map<String, String>> authorize(HttpServletRequest request) throws UnsupportedEncodingException {
        String apiUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        String authorizationUrl = "https://login.microsoftonline.com/" + this.tenantId +
                "/oauth2/v2.0/authorize?client_id=" + this.clientId +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(apiUrl + "/callback", "UTF-8") +
                "&response_mode=query" +
                "&scope=https://vault.azure.net/.default" +
                "&state=12345";

        return ResponseEntity.ok(Collections.singletonMap("nextLink", authorizationUrl));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) {
        try {
            String apiUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            String url = "https://login.microsoftonline.com/" + this.tenantId + "/oauth2/v2.0/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> inputMap = new LinkedMultiValueMap<>();
            inputMap.add("grant_type", "authorization_code");
            inputMap.add("client_id", this.clientId);
            inputMap.add("client_secret", this.clientSecret);
            inputMap.add("code", code);
            inputMap.add("redirect_uri", apiUrl + "/callback");
            inputMap.add("scope", "https://vault.azure.net/.default");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(inputMap, headers);
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> tokenResponse = template.postForEntity(url, entity, String.class);
            JSONObject myJson = new JSONObject(tokenResponse.getBody());
            String accessToken = myJson.getString("access_token");

            String encryptedToken = cryptoUtil.encryptWithAES(accessToken);

            String frontendRedirectUrl = this.frontendURI + "/login-success?token=" + URLEncoder.encode(encryptedToken, "UTF-8");

            response.sendRedirect(frontendRedirectUrl);
        } catch (Exception e) {
            throw new CryptoException(e.getMessage());
        }
    }

}
