package com.toolschallenge.tools_challenge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(erro(ex.getStatus(), ex.getMessage()));
    }

    private Map<String, Object> erro(HttpStatus status, String mensagem) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "mensagem", mensagem
        );
    }
}
