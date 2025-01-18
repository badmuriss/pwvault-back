package com.outis.pwvault.controller;

import com.outis.pwvault.dto.SecretCreateRequest;
import com.outis.pwvault.dto.SecretDetailResponse;
import com.outis.pwvault.dto.SecretListResponse;
import com.outis.pwvault.dto.SecretUpdateRequest;
import com.outis.pwvault.mapper.SecretMapper;
import com.outis.pwvault.service.SecretService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/secrets")
public class SecretController {

    private final SecretService secretService;
    private final SecretMapper mapper;

    public SecretController(SecretService secretService, SecretMapper mapper){
        this.secretService = secretService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<SecretListResponse>> listALL(){
        return ResponseEntity.ok(secretService.listAll().stream().map(mapper::toListResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecretDetailResponse> getSecretDetail(@PathVariable String id){
        return ResponseEntity.ok(mapper.toDetailResponse(secretService.getDetails(id)));
    }

    @PostMapping
    public ResponseEntity<SecretDetailResponse> create(@Valid @RequestBody SecretCreateRequest secretCreateRequest){
        return new ResponseEntity<SecretDetailResponse>(mapper.toDetailResponse(secretService.create(mapper.toDto(secretCreateRequest))), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SecretDetailResponse> update(@PathVariable String id, @Valid @RequestBody SecretUpdateRequest secretUpdateRequest){
        return ResponseEntity.ok(mapper.toDetailResponse(secretService.update(id, secretUpdateRequest)));
    }

}
