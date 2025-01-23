package com.outis.pwvault.dto;

import lombok.Builder;

import javax.validation.constraints.Size;

@Builder
public record SecretUpdateRequest(
        @Size(max = 20)
        String name,

        @Size(max = 20)
        String folder,

        @Size(max = 255)
        String value
) {
}
