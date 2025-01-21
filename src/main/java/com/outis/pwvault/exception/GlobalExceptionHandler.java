package com.outis.pwvault.exception;

import com.outis.pwvault.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UnauthenticatedException.class})
    public ResponseEntity<ErrorResponse> handleUnauthenticatedException(UnauthenticatedException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        LocalDateTime.now().toString(),
                        String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        "User not authenticated"
                ));
    }

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

    @ExceptionHandler({CryptoException.class})
    public ResponseEntity<ErrorResponse> handleCryptoException(CryptoException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        LocalDateTime.now().toString(),
                        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler({SecretNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleSecretNotFoundException(SecretNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        LocalDateTime.now().toString(),
                        String.valueOf(HttpStatus.NOT_FOUND.value()),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        exception.getMessage()
                ));
    }
}
