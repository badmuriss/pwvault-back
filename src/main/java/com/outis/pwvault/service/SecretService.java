package com.outis.pwvault.service;

import com.outis.pwvault.dto.SecretDto;
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

    public SecretDto getDetails(){
        return null;
    }

    public SecretDto update(){
        return null;
    }

    public SecretDto create(){
        return null;
    }

}
