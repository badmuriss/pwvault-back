package com.outis.pwvault.controller;

import com.outis.pwvault.dto.SecretCreateRequest;
import com.outis.pwvault.dto.SecretDetailResponse;
import com.outis.pwvault.dto.SecretListResponse;
import com.outis.pwvault.dto.SecretUpdateRequest;
import com.outis.pwvault.mapper.SecretMapper;
import com.outis.pwvault.service.SecretService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
    public ResponseEntity<List<SecretListResponse>> listALL(HttpServletRequest request){
        String token = (String) request.getAttribute("accessToken");
        return ResponseEntity.ok(secretService.listAll(token).stream().map(mapper::toListResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecretDetailResponse> getSecretDetail(HttpServletRequest request, @PathVariable @NotBlank String id){
        String token = (String) request.getAttribute("accessToken");
        return ResponseEntity.ok(mapper.toDetailResponse(secretService.getDetails(token, id)));
    }

    @PostMapping
    public ResponseEntity<SecretDetailResponse> create(HttpServletRequest request, @Valid @RequestBody SecretCreateRequest secretCreateRequest){
        String token = (String) request.getAttribute("accessToken");
        return new ResponseEntity<SecretDetailResponse>(mapper.toDetailResponse(secretService.create(token, mapper.toDto(secretCreateRequest))), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SecretDetailResponse> update(HttpServletRequest request, @PathVariable @NotBlank String id, @Valid @RequestBody SecretUpdateRequest secretUpdateRequest){
        String token = (String) request.getAttribute("accessToken");
        return ResponseEntity.ok(mapper.toDetailResponse(secretService.update(token, id, secretUpdateRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable @NotBlank String id){
        String token = (String) request.getAttribute("accessToken");
        secretService.delete(token, id);
        return ResponseEntity.noContent().build();
    }
}
