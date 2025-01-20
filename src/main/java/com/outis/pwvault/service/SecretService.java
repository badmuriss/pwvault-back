package com.outis.pwvault.service;

import com.outis.pwvault.client.AzureClient;
import com.outis.pwvault.dto.SecretDto;
import com.outis.pwvault.dto.SecretUpdateRequest;
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
        SecretDto secretDto = mapper.toDto(secretUpdateRequest, getDetails(token, id));
        return mapper.toDto(azureClient.setSecret(token, secretDto));
    }

    public void delete(String token, String id){
        azureClient.deleteSecret(token, id);
    }
}
