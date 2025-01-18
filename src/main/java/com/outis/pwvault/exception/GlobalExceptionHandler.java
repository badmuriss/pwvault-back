package com.outis.pwvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AzureAccessDeniedException.class})
    public ResponseEntity<Object> handleAzureAcessDeniedException(AzureAccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("You don't have the permissions to see or edit secrets on this KeyVault");
    }

}
