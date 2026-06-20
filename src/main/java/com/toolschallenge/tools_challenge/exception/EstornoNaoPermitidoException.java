package com.toolschallenge.tools_challenge.exception;

import org.springframework.http.HttpStatus;

public class EstornoNaoPermitidoException extends ApiException {

    private EstornoNaoPermitidoException(String mensagem) {
        super(HttpStatus.BAD_REQUEST, mensagem);
    }

    public static EstornoNaoPermitidoException transacaoJaCancelada() {
        return new EstornoNaoPermitidoException("Transação já está cancelada.");
    }

    public static EstornoNaoPermitidoException transacaoNegada() {
        return new EstornoNaoPermitidoException("Não é possível estornar uma transação negada.");
    }
}
