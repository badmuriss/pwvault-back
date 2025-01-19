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

    SecretDto mock = new SecretDto("test","abc","/folder","pass");

    public SecretService(SecretMapper mapper, AzureClient azureClient){
        this.mapper = mapper;
        this.azureClient = azureClient;
    }

    public List<SecretDto> listAll(String token){
        var test = azureClient.getSecrets(token);
        return List.of(mock);
    }

    public SecretDto getDetails(String token, String id){
        return mock;
    }

    public SecretDto create(String token, SecretDto secretDto){
        return mock;
    }

    public SecretDto update(String token, String id, SecretUpdateRequest secretUpdateRequest) {
        return mock;
    }

    public void delete(String token, String id){

    }
}
