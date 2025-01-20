package com.outis.pwvault.mapper;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import com.outis.pwvault.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {TagMapper.class}
)
public interface SecretMapper {

    SecretMapper INSTANCE = Mappers.getMapper(SecretMapper.class);

    @Mapping(target = "id", expression = "java(com.outis.pwvault.util.IDGeneratorUtil.GetBase62(7))")
    SecretDto toDto(SecretCreateRequest secretCreateRequest);

    SecretDto toDto(SecretUpdateRequest secretUpdateRequest);

    SecretDto toDto(SecretUpdateRequest secretUpdateRequest, @MappingTarget SecretDto secretDto);

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