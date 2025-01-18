package com.outis.pwvault.dto;

import lombok.Builder;

@Builder
public record SecretDetailResponse(
        String id,
        String name,
        String folder,
        String value
) {
}
