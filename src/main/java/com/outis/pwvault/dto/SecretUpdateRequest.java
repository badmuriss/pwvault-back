package com.outis.pwvault.dto;

import lombok.Builder;

@Builder
public record SecretUpdateRequest(
        String name,
        String folder,
        String value
) {
}
