package com.toolschallenge.tools_challenge.exception;

import org.springframework.http.HttpStatus;

public class TransacaoNaoEncontradaException extends ApiException {

    public TransacaoNaoEncontradaException(String id) {
        super(HttpStatus.NOT_FOUND, "Transação não encontrada para o id: " + id);
    }
}
