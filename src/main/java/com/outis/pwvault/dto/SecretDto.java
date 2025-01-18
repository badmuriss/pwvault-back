package com.outis.pwvault.dto;

import lombok.Builder;

@Builder
public record SecretDto(
        String id,
        String name,
        String folder,
        String value
) {
}
