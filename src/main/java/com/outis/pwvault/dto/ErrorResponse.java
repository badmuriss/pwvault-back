package com.outis.pwvault.dto;

public record ErrorResponse(
        String timestamp,
        String status,
        String error,
        String message
) {
}
