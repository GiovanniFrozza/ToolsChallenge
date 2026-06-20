package com.toolschallenge.tools_challenge.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.toolschallenge.tools_challenge.exception.RequisicaoInvalidaException;

public enum TipoPagamento {

    AVISTA("AVISTA"),
    PARCELADO_LOJA("PARCELADO LOJA"),
    PARCELADO_EMISSOR("PARCELADO EMISSOR");

    private final String valor;

    TipoPagamento(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static TipoPagamento fromValor(String valor) {
        for (TipoPagamento tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new RequisicaoInvalidaException("Tipo de pagamento inválido: " + valor);
    }
}
