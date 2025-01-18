package com.outis.pwvault.mapper;

import com.outis.pwvault.dto.*;
import com.outis.pwvault.model.Secret;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SecretMapper {

    SecretMapper INSTANCE = Mappers.getMapper(SecretMapper.class);

    Secret toEntity(SecretDto secretDto);

    SecretDto toDto(Secret secret);

    @Mapping(target = "id", ignore = true)
    SecretDto toDto(SecretCreateRequest secretCreateRequest);

    SecretDto toDto(SecretUpdateRequest secretUpdateRequest);

    SecretDto toDto(SecretUpdateRequest secretUpdateRequest, @MappingTarget SecretDto secretDto);

    SecretListResponse toListResponse(SecretDto secretDto);

    SecretDetailResponse toDetailResponse(SecretDto secretDto);
}
