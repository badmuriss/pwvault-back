package com.outis.pwvault.model;

public record JwtPayload(
        String keyVaultToken,
        String managementToken,
        String accountId
) {
}
