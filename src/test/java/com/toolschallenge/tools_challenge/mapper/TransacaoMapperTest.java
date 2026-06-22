package com.toolschallenge.tools_challenge.mapper;

import com.toolschallenge.tools_challenge.dto.request.TransacaoRequest;
import com.toolschallenge.tools_challenge.dto.response.PagamentoResponse;
import com.toolschallenge.tools_challenge.entity.Transacao;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import com.toolschallenge.tools_challenge.support.PagamentoTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransacaoMapperTest {

    private TransacaoMapper transacaoMapper;

    @BeforeEach
    void setUp() {
        transacaoMapper = new TransacaoMapper();
    }

    @Test
    void deveConverterEntidadeParaResponse() {
        Transacao transacao = PagamentoTestHelper.transacaoAutorizada();

        PagamentoResponse response = transacaoMapper.toResponse(transacao);

        assertThat(response.getTransacao().getId()).isEqualTo(PagamentoTestHelper.ID_TRANSACAO);
        assertThat(response.getTransacao().getCartao()).isEqualTo("4444********1234");
        assertThat(response.getTransacao().getDescricao().getValor()).isEqualByComparingTo("500.50");
        assertThat(response.getTransacao().getDescricao().getStatus()).isEqualTo(StatusTransacao.AUTORIZADO);
        assertThat(response.getTransacao().getFormaPagamento().getParcelas()).isEqualTo(1);
    }

    @Test
    void deveConverterRequestParaEntidade() {
        TransacaoRequest request = PagamentoTestHelper.pagamentoValido().getTransacao();

        Transacao transacao = transacaoMapper.toEntity(
                request,
                StatusTransacao.AUTORIZADO,
                "1234567890",
                "147258369"
        );

        assertThat(transacao.getId()).isEqualTo(PagamentoTestHelper.ID_TRANSACAO);
        assertThat(transacao.getDescricao().getNsu()).isEqualTo("1234567890");
        assertThat(transacao.getDescricao().getCodigoAutorizacao()).isEqualTo("147258369");
        assertThat(transacao.getDescricao().getStatus()).isEqualTo(StatusTransacao.AUTORIZADO);
    }
}
