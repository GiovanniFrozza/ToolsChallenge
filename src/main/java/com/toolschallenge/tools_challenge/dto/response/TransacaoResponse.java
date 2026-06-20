package com.toolschallenge.tools_challenge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoResponse {

    private String cartao;
    private String id;
    private DescricaoResponse descricao;
    private FormaPagamentoResponse formaPagamento;
}
