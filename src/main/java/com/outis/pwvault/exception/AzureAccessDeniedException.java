package com.outis.pwvault.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class AzureAccessDeniedException extends BadCredentialsException {
    public AzureAccessDeniedException(String msg) {
        super(msg);
    }
}
