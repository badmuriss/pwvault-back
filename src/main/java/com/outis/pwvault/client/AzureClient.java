package com.outis.pwvault.client;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import com.outis.pwvault.dto.SecretDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class AzureClient {

    @Value("${KEYVAULT_URL:https://default-keyvault.vault.azure.net/}")
    private String keyVaultUrl;

    public List<SecretProperties> getSecrets(String token) {
        TokenCredential userTokenCredential = request -> Mono.just(new AccessToken(token, OffsetDateTime.now().plusHours(1)));

        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(userTokenCredential)
                .buildClient();

        return secretClient.listPropertiesOfSecrets().stream().toList();
    }

    public KeyVaultSecret getSecretDetails(String token, String id){
        TokenCredential userTokenCredential = request -> Mono.just(new AccessToken(token, OffsetDateTime.now().plusHours(1)));

        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(userTokenCredential)
                .buildClient();

        return secretClient.getSecret(id);
    }

    public KeyVaultSecret setSecret(String token, SecretDto secretDto){
        TokenCredential userTokenCredential = request -> Mono.just(new AccessToken(token, OffsetDateTime.now().plusHours(1)));

        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(userTokenCredential)
                .buildClient();

        KeyVaultSecret secret = secretClient.setSecret(secretDto.id(), secretDto.value());

        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        if(secretDto.name() != null){
            map.put("name", secretDto.name());
        }
        if(secretDto.folder() != null){
            map.put("folder", secretDto.folder());
        }
        secret.setProperties(secret.getProperties().setTags(map));
        secretClient.updateSecretProperties(secret.getProperties());

        return secret;
    }

    public void deleteSecret(String token, String id){
        TokenCredential userTokenCredential = request -> Mono.just(new AccessToken(token, OffsetDateTime.now().plusHours(1)));

        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(userTokenCredential)
                .buildClient();

        var deletedSecret = secretClient.beginDeleteSecret(id);
        deletedSecret.waitForCompletion();
    }
}
