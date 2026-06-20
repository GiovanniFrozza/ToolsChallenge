package com.toolschallenge.tools_challenge.dto.request;

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
public class TransacaoRequest {

    private String cartao;
    private String id;
    private DescricaoRequest descricao;
    private FormaPagamentoRequest formaPagamento;
}
