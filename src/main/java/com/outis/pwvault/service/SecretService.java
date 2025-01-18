package com.outis.pwvault.service;

import com.outis.pwvault.dto.SecretDto;
import com.outis.pwvault.dto.SecretUpdateRequest;
import com.outis.pwvault.mapper.SecretMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecretService {

    SecretMapper mapper;

    public SecretService(SecretMapper mapper){
        this.mapper = mapper;
    }

    public List<SecretDto> listAll(){
        return null;
    }

    public SecretDto getDetails(String id){
        return null;
    }

    public SecretDto create(SecretDto secretDto){
        return null;
    }

    public SecretDto update(String id, SecretUpdateRequest secretUpdateRequest) {
        return null;
    }
}
