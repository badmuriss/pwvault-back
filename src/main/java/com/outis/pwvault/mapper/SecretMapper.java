package com.outis.pwvault.mapper;

import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import com.azure.security.keyvault.secrets.models.SecretProperties;
import com.outis.pwvault.dto.*;
import com.outis.pwvault.util.CryptoUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
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
    @Mapping(target = "value", expression = "java(cryptoUtil.encryptWithAES(secretCreateRequest.value()))")
    SecretDto toDto(SecretCreateRequest secretCreateRequest, CryptoUtil cryptoUtil);

    @Mapping(target = "name", expression = "java(java.util.Optional.ofNullable(secretUpdateRequest.name()).orElse(existingSecretDto.name()))")
    @Mapping(target = "folder", expression = "java(java.util.Optional.ofNullable(secretUpdateRequest.folder()).orElse(existingSecretDto.folder()))")
    @Mapping(target = "value", expression = "java(java.util.Optional.ofNullable(secretUpdateRequest.value()).map(val -> cryptoUtil.encryptWithAES(val)).orElse(existingSecretDto.value()))")
    @Mapping(target = "id", source = "existingSecretDto.id")
    SecretDto toDto(SecretUpdateRequest secretUpdateRequest, SecretDto existingSecretDto, CryptoUtil cryptoUtil);


    SecretListResponse toListResponse(SecretDto secretDto);

    SecretDetailResponse toDetailResponse(SecretDto secretDto);

    @Mapping(source = "keyVaultSecret.properties.name", target = "id")
    @Mapping(source = "keyVaultSecret.properties.tags", target = "name", qualifiedByName = "mapName")
    @Mapping(source = "keyVaultSecret.properties.tags", target = "folder", qualifiedByName = "mapFolder")
    @Mapping(target = "value", expression = "java(cryptoUtil.decryptWithAES(keyVaultSecret.getValue()))")
    SecretDto toDto(KeyVaultSecret keyVaultSecret, CryptoUtil cryptoUtil);

    @Mapping(target = "value", ignore = true)
    @Mapping(source = "name", target = "id")
    @Mapping(source = "tags", target = "folder", qualifiedByName = "mapFolder")
    @Mapping(source = "tags", target = "name", qualifiedByName = "mapName")
    SecretDto toDto(SecretProperties secret);


}