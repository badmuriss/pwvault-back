package com.outis.pwvault.exception;

public class SecretNotFoundException extends RuntimeException {
    public SecretNotFoundException(String msg) {
        super(msg);
    }
}
