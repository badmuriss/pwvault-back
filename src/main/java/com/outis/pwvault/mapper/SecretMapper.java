package com.outis.pwvault.mapper;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import com.outis.pwvault.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {TagMapper.class}
)
public interface SecretMapper {

    SecretMapper INSTANCE = Mappers.getMapper(SecretMapper.class);

    @Mapping(target = "id", expression = "java(com.outis.pwvault.util.IDGeneratorUtil.GetBase62(8))")
    SecretDto toDto(SecretCreateRequest secretCreateRequest);

    SecretDto toDto(SecretUpdateRequest secretUpdateRequest);

    @Mapping(target = "name", expression = "java(secretUpdateRequest.name() != null ? secretUpdateRequest.name() : existingSecretDto.name())")
    @Mapping(target = "folder", expression = "java(secretUpdateRequest.folder() != null ? secretUpdateRequest.folder() : existingSecretDto.folder())")
    @Mapping(target = "value", expression = "java(secretUpdateRequest.value() != null ? secretUpdateRequest.value() : existingSecretDto.value())")
    @Mapping(target = "id", expression = "java(existingSecretDto.id())")
    SecretDto toDto(SecretUpdateRequest secretUpdateRequest, SecretDto existingSecretDto);

    SecretListResponse toListResponse(SecretDto secretDto);

    SecretDetailResponse toDetailResponse(SecretDto secretDto);

    @Mapping(source = "properties.name", target = "id")
    @Mapping(source = "properties.tags", target = "name", qualifiedByName = "mapName")
    @Mapping(source = "properties.tags", target = "folder", qualifiedByName = "mapFolder")
    @Mapping(source = "value", target = "value")
    SecretDto toDto(KeyVaultSecret keyVaultSecret);

    @Mapping(target = "value", ignore = true)
    @Mapping(source = "name", target = "id")
    @Mapping(source = "tags", target = "folder", qualifiedByName = "mapFolder")
    @Mapping(source = "tags", target = "name", qualifiedByName = "mapName")
    SecretDto toDto(SecretProperties secret);


}