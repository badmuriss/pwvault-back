package com.outis.pwvault.service;

import com.outis.pwvault.dto.SecretDto;
import com.outis.pwvault.dto.SecretUpdateRequest;
import com.outis.pwvault.mapper.SecretMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecretService {

    SecretMapper mapper;
    SecretDto mock = new SecretDto("test","abc","/folder","pass");
    public SecretService(SecretMapper mapper){
        this.mapper = mapper;
    }

    public List<SecretDto> listAll(){
        return List.of(mock);
    }

    public SecretDto getDetails(String id){
        return mock;
    }

    public SecretDto create(SecretDto secretDto){
        return mock;
    }

    public SecretDto update(String id, SecretUpdateRequest secretUpdateRequest) {
        return mock;
    }
}
