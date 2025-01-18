package com.outis.pwvault.dto;

import lombok.Builder;

@Builder
public record SecretListResponse (
        String id,
        String name,
        String folder
) {

}
