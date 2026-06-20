package com.toolschallenge.tools_challenge.dto.request;

import com.toolschallenge.tools_challenge.enums.TipoPagamento;
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
public class FormaPagamentoRequest {

    private TipoPagamento tipo;
    private Integer parcelas;
}
