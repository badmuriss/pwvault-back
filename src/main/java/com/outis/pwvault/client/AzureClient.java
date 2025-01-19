package com.outis.pwvault.client;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AzureClient {

    @Value("${KEYVAULT_URL}")
    private String keyVaultUrl;

    public List<SecretProperties> getSecrets(String token) {
        TokenCredential userTokenCredential = request -> {
            return Mono.just(new AccessToken(token, OffsetDateTime.now().plusHours(1)));
        };

        // Build the Key Vault client
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(userTokenCredential)
                .buildClient();

        // Retrieve the secret
        return secretClient.listPropertiesOfSecrets().stream().toList();
    }

}
