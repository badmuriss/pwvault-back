package com.outis.pwvault.service;

import com.outis.pwvault.client.AzureClient;
import com.outis.pwvault.dto.SecretDto;
import com.outis.pwvault.dto.SecretUpdateRequest;
import com.outis.pwvault.exception.SecretNotFoundException;
import com.outis.pwvault.mapper.SecretMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecretService {

    SecretMapper mapper;
    AzureClient azureClient;

    public SecretService(SecretMapper mapper, AzureClient azureClient){
        this.mapper = mapper;
        this.azureClient = azureClient;
    }

    public List<SecretDto> listAll(String token){
        return azureClient.getSecrets(token).stream().map(mapper::toDto).toList();
    }

    public SecretDto getDetails(String token, String id){
        return mapper.toDto(azureClient.getSecretDetails(token, id));
    }

    public SecretDto create(String token, SecretDto secretDto){
        return mapper.toDto(azureClient.setSecret(token, secretDto));
    }

    public SecretDto update(String token, String id, SecretUpdateRequest secretUpdateRequest) {
        try {
            SecretDto existingSecret = getDetails(token, id);
            SecretDto secretDto = mapper.toDto(secretUpdateRequest, existingSecret);
            return mapper.toDto(azureClient.setSecret(token, secretDto));
        } catch (Exception e){
            throw new SecretNotFoundException("Secret with id " + id + " not found");
        }
    }

    public void delete(String token, String id){
        azureClient.deleteSecret(token, id);
    }
}
