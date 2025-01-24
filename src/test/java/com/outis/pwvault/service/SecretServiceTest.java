package com.outis.pwvault.service;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import com.outis.pwvault.client.AzureClient;
import com.outis.pwvault.dto.SecretDto;
import com.outis.pwvault.dto.SecretUpdateRequest;
import com.outis.pwvault.exception.SecretNotFoundException;
import com.outis.pwvault.mapper.SecretMapper;
import com.outis.pwvault.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class SecretServiceTest {

    @Mock
    private SecretMapper mapper;

    @Mock
    private AzureClient azureClient;

    @Mock
    private CryptoUtil cryptoUtil;

    @InjectMocks
    private SecretService secretService;

    @Test
    void listAll_shouldReturnListOfSecrets() {
        String token = "test-token";
        SecretDto secretDto = new SecretDto("id","name","folder","value");
        when(azureClient.getSecrets(token)).thenReturn(List.of(new KeyVaultSecret("id", "value").getProperties()));
        when(mapper.toDto(any(KeyVaultSecret.class), any(CryptoUtil.class))).thenReturn(secretDto);

        List<SecretDto> result = secretService.listAll(token);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(azureClient).getSecrets(token);
        verify(mapper).toDto(any(SecretProperties.class));
    }

    @Test
    void getDetails_shouldReturnSecretDetails() {
        String token = "test-token";
        String id = "test-id";
        SecretDto secretDto = new SecretDto("id","name","folder","value");
        when(azureClient.getSecretDetails(token, id)).thenReturn(new KeyVaultSecret("id","value"));
        when(mapper.toDto(any(KeyVaultSecret.class), any(CryptoUtil.class))).thenReturn(secretDto);

        SecretDto result = secretService.getDetails(token, id);

        assertNotNull(result);
        verify(azureClient).getSecretDetails(token, id);
        verify(mapper).toDto(any(KeyVaultSecret.class), any(CryptoUtil.class));
    }

    @Test
    void create_shouldReturnCreatedSecret() {
        String token = "test-token";
        SecretDto secretDto = new SecretDto("id","name","folder","value");
        when(azureClient.setSecret(token, secretDto)).thenReturn(new KeyVaultSecret("id", "value"));
        when(mapper.toDto(any(KeyVaultSecret.class), any(CryptoUtil.class))).thenReturn(secretDto);

        SecretDto result = secretService.create(token, secretDto);

        assertNotNull(result);
        verify(azureClient).setSecret(token, secretDto);
        verify(mapper).toDto(any(KeyVaultSecret.class), any(CryptoUtil.class));
    }

    @Test
    void update_shouldReturnUpdatedSecret() {
        String token = "test-token";
        String id = "test-id";
        SecretDto existingSecret = new SecretDto("id", "name", "folder", "value");
        SecretDto updatedSecret = new SecretDto("id", "updatedName", "folder", "updatedValue");

        SecretUpdateRequest updateRequest = new SecretUpdateRequest("updatedName", "folder", "updatedValue");
        KeyVaultSecret keyVaultSecret = new KeyVaultSecret("id", "updatedValue");

        when(azureClient.getSecretDetails(token, id)).thenReturn(new KeyVaultSecret("id", "value"));
        when(mapper.toDto(any(KeyVaultSecret.class), any(CryptoUtil.class))).thenReturn(existingSecret);

        when(mapper.toDto(updateRequest, existingSecret, this.cryptoUtil)).thenReturn(updatedSecret);

        when(azureClient.setSecret(token, updatedSecret)).thenReturn(keyVaultSecret);

        when(mapper.toDto(keyVaultSecret, this.cryptoUtil)).thenReturn(updatedSecret);

        SecretDto result = secretService.update(token, id, updateRequest);

        assertNotNull(result);
        assertEquals("updatedName", result.name());
        assertEquals("updatedValue", result.value());

        verify(azureClient).getSecretDetails(token, id);
        verify(mapper).toDto(updateRequest, existingSecret, cryptoUtil);
        verify(azureClient).setSecret(token, updatedSecret);
    }



    @Test
    void update_shouldThrowExceptionWhenSecretNotFound() {
        String token = "test-token";
        String id = "non-existent-id";
        SecretUpdateRequest request = new SecretUpdateRequest("name", "folder", "value");

        when(azureClient.getSecretDetails(token, id)).thenThrow(new SecretNotFoundException("Secret not found"));

        assertThrows(SecretNotFoundException.class, () -> secretService.update(token, id, request));

        verify(azureClient).getSecretDetails(token, id);
        verifyNoMoreInteractions(mapper, azureClient);
    }

    @Test
    void delete_shouldCallAzureClientDeleteSecret() {
        String token = "test-token";
        String id = "test-id";

        doNothing().when(azureClient).deleteSecret(token, id);

        secretService.delete(token, id);

        verify(azureClient).deleteSecret(token, id);
    }
}
