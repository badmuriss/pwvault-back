package com.outis.pwvault.exception;

import com.outis.pwvault.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AzureAccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleAzureAcessDeniedException(AzureAccessDeniedException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        LocalDateTime.now().toString(),
                        String.valueOf(HttpStatus.FORBIDDEN.value()),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        "You don't have the permissions to see or edit secrets on this KeyVault"
                ));
    }
}
