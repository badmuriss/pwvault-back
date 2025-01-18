package com.outis.pwvault.mapper;

import com.outis.pwvault.dto.*;
import com.outis.pwvault.model.Secret;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SecretMapper {

    SecretMapper INSTANCE = Mappers.getMapper(SecretMapper.class);

    Secret toEntity(SecretDto secretDto);

    SecretDto toDto(Secret secret);

    @Mapping(target = "id", ignore = true) // id is typically generated later
    SecretDto toDto(SecretCreateRequest secretCreateRequest);

    SecretDto toDto(SecretUpdateRequest secretUpdateRequest);

    @Mapping(target = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "folder", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "value", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SecretDto toDto(SecretUpdateRequest secretUpdateRequest, @MappingTarget SecretDto secretDto);

    SecretListResponse toListResponse(SecretDto secretDto);

    SecretDetailResponse toDetailResponse(SecretDto secretDto);
}
