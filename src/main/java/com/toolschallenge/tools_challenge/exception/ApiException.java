package com.toolschallenge.tools_challenge.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;

    protected ApiException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
