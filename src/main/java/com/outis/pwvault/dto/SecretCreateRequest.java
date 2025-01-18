package com.outis.pwvault.dto;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Builder
public record SecretCreateRequest(
        @NotBlank
        String name,

        @NotBlank
        String folder,

        @NotBlank
        String value
) {
}
