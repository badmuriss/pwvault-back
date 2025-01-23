package com.outis.pwvault.dto;

import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
public record SecretCreateRequest(

        @Size(max = 20)
        @NotBlank
        String name,

        @Size(max = 30)
        @NotBlank
        String folder,

        @Size(max = 255)
        @NotBlank
        String value
) {
}
