package com.outis.pwvault.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outis.pwvault.dto.*;
import com.outis.pwvault.mapper.SecretMapper;
import com.outis.pwvault.service.SecretService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecretControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecretService secretService;

    @MockitoBean
    private SecretMapper mapper;

    @Test
    void listALL_shouldReturnListOfSecrets() throws Exception {
        SecretDto secretDto = new SecretDto("id", "name", "folder", "value");
        Mockito.when(secretService.listAll(anyString())).thenReturn(List.of(secretDto));
        Mockito.when(mapper.toListResponse(any(SecretDto.class))).thenReturn(new SecretListResponse("id", "name", "folder"));

        mockMvc.perform(get("/secrets")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("id"))
                .andExpect(jsonPath("$[0].name").value("name"));
    }

    @Test
    void getSecretDetail_shouldReturnSecretDetail() throws Exception {
        SecretDto secretDto = new SecretDto("id", "name", "folder", "value");
        SecretDetailResponse secretDetailResponse = new SecretDetailResponse("id", "name", "folder", "value");

        Mockito.when(secretService.getDetails(anyString(), anyString())).thenReturn(secretDto);
        Mockito.when(mapper.toDetailResponse(any(SecretDto.class))).thenReturn(secretDetailResponse);

        mockMvc.perform(get("/secrets/{id}", "test-id")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void create_shouldReturnCreatedSecret() throws Exception {
        SecretDto secretDto = new SecretDto("id", "name", "folder", "value");
        SecretDetailResponse secretDetailResponse = new SecretDetailResponse("id", "name", "folder", "value");
        SecretCreateRequest secretCreateRequest = new SecretCreateRequest("name", "folder", "value");

        Mockito.when(secretService.create(anyString(), any(SecretDto.class))).thenReturn(secretDto);
        Mockito.when(mapper.toDto(any(SecretCreateRequest.class))).thenReturn(secretDto);
        Mockito.when(mapper.toDetailResponse(any(SecretDto.class))).thenReturn(secretDetailResponse);

        mockMvc.perform(post("/secrets")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secretCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void update_shouldReturnUpdatedSecret() throws Exception {
        SecretDto secretDto = new SecretDto("id", "name", "folder", "value");
        SecretDetailResponse secretDetailResponse = new SecretDetailResponse("id", "name", "folder", "value");
        SecretUpdateRequest secretUpdateRequest = new SecretUpdateRequest("name", "folder", "value");

        Mockito.when(secretService.update(anyString(), anyString(), any(SecretUpdateRequest.class))).thenReturn(secretDto);
        Mockito.when(mapper.toDetailResponse(any(SecretDto.class))).thenReturn(secretDetailResponse);

        mockMvc.perform(patch("/secrets/{id}", "test-id")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secretUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("id"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/secrets/{id}", "test-id")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
