package com.toolschallenge.tools_challenge.exception;

import org.springframework.http.HttpStatus;

public class TransacaoJaExisteException extends ApiException {

    public TransacaoJaExisteException(String id) {
        super(HttpStatus.CONFLICT, "Já existe uma transação cadastrada com o id: " + id);
    }
}
