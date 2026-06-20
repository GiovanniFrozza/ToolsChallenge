package com.toolschallenge.tools_challenge.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.toolschallenge.tools_challenge.exception.RequisicaoInvalidaException;

public enum StatusTransacao {

    AUTORIZADO("AUTORIZADO"),
    NEGADO("NEGADO"),
    CANCELADO("CANCELADO");

    private final String valor;

    StatusTransacao(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static StatusTransacao fromValor(String valor) {
        for (StatusTransacao status : values()) {
            if (status.valor.equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new RequisicaoInvalidaException("Status de transação inválido: " + valor);
    }
}
