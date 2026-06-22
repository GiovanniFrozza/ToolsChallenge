package com.toolschallenge.tools_challenge.support;

import com.toolschallenge.tools_challenge.dto.request.DescricaoRequest;
import com.toolschallenge.tools_challenge.dto.request.FormaPagamentoRequest;
import com.toolschallenge.tools_challenge.dto.request.PagamentoRequest;
import com.toolschallenge.tools_challenge.dto.request.TransacaoRequest;
import com.toolschallenge.tools_challenge.entity.Descricao;
import com.toolschallenge.tools_challenge.entity.FormaPagamento;
import com.toolschallenge.tools_challenge.entity.Transacao;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import com.toolschallenge.tools_challenge.enums.TipoPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PagamentoTestHelper {

    public static final String ID_TRANSACAO = "100023568900001";
    public static final LocalDateTime DATA_HORA = LocalDateTime.of(2021, 5, 1, 18, 30, 0);

    private PagamentoTestHelper() {
    }

    public static PagamentoRequest pagamentoValido() {
        return pagamentoComId(ID_TRANSACAO);
    }

    public static PagamentoRequest pagamentoComId(String id) {
        return PagamentoRequest.builder()
                .transacao(TransacaoRequest.builder()
                        .id(id)
                        .cartao("4444********1234")
                        .descricao(DescricaoRequest.builder()
                                .valor(new BigDecimal("500.50"))
                                .dataHora(DATA_HORA)
                                .estabelecimento("PetShop Mundo cão")
                                .build())
                        .formaPagamento(FormaPagamentoRequest.builder()
                                .tipo(TipoPagamento.AVISTA)
                                .parcelas(1)
                                .build())
                        .build())
                .build();
    }

    public static PagamentoRequest pagamentoComValor(BigDecimal valor) {
        PagamentoRequest request = pagamentoValido();
        request.getTransacao().getDescricao().setValor(valor);
        return request;
    }

    public static PagamentoRequest pagamentoParcelado() {
        PagamentoRequest request = pagamentoValido();
        request.getTransacao().getFormaPagamento().setTipo(TipoPagamento.PARCELADO_LOJA);
        request.getTransacao().getFormaPagamento().setParcelas(3);
        return request;
    }

    public static Transacao transacaoAutorizada() {
        return Transacao.builder()
                .idTransacao(1L)
                .id(ID_TRANSACAO)
                .cartao("4444********1234")
                .descricao(Descricao.builder()
                        .valor(new BigDecimal("500.50"))
                        .dataHora(DATA_HORA)
                        .estabelecimento("PetShop Mundo cão")
                        .nsu("1234567890")
                        .codigoAutorizacao("147258369")
                        .status(StatusTransacao.AUTORIZADO)
                    .build())
                .formaPagamento(FormaPagamento.builder()
                        .tipo(TipoPagamento.AVISTA)
                        .parcelas(1)
                    .build())
                .build();
    }

    public static Transacao transacaoComStatus(StatusTransacao status) {
        Transacao transacao = transacaoAutorizada();
        transacao.getDescricao().setStatus(status);
        if (status == StatusTransacao.NEGADO) {
            transacao.getDescricao().setNsu(null);
            transacao.getDescricao().setCodigoAutorizacao(null);
        }
        return transacao;
    }
}
