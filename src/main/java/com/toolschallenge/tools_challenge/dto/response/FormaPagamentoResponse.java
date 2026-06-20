package com.toolschallenge.tools_challenge.dto.response;

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
public class FormaPagamentoResponse {

    private TipoPagamento tipo;
    private Integer parcelas;
}
