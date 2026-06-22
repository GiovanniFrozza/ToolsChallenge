package com.toolschallenge.tools_challenge.service;

import com.toolschallenge.tools_challenge.dto.request.PagamentoRequest;
import com.toolschallenge.tools_challenge.dto.response.PagamentoResponse;
import com.toolschallenge.tools_challenge.entity.Transacao;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import com.toolschallenge.tools_challenge.exception.EstornoNaoPermitidoException;
import com.toolschallenge.tools_challenge.exception.RequisicaoInvalidaException;
import com.toolschallenge.tools_challenge.exception.TransacaoJaExisteException;
import com.toolschallenge.tools_challenge.exception.TransacaoNaoEncontradaException;
import com.toolschallenge.tools_challenge.mapper.TransacaoMapper;
import com.toolschallenge.tools_challenge.repository.TransacaoRepository;
import com.toolschallenge.tools_challenge.support.PagamentoTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        transacaoService = new TransacaoService(transacaoRepository, new TransacaoMapper());
    }

    @Test
    void deveAutorizarPagamentoValido() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        when(transacaoRepository.existsById(PagamentoTestHelper.ID_TRANSACAO)).thenReturn(false);
        mockSave();

        PagamentoResponse response = transacaoService.processarPagamento(request);

        assertThat(response.getTransacao().getDescricao().getStatus()).isEqualTo(StatusTransacao.AUTORIZADO);
        assertThat(response.getTransacao().getDescricao().getNsu()).isNotBlank();
        assertThat(response.getTransacao().getDescricao().getCodigoAutorizacao()).isNotBlank();
    }

    @Test
    void deveNegarPagamentoComValorInvalido() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoComValor(BigDecimal.ZERO);
        when(transacaoRepository.existsById(PagamentoTestHelper.ID_TRANSACAO)).thenReturn(false);
        mockSave();

        PagamentoResponse response = transacaoService.processarPagamento(request);

        assertThat(response.getTransacao().getDescricao().getStatus()).isEqualTo(StatusTransacao.NEGADO);
    }

    @Test
    void deveLancarExcecaoQuandoIdJaExistir() {
        when(transacaoRepository.existsById(PagamentoTestHelper.ID_TRANSACAO)).thenReturn(true);

        assertThatThrownBy(() -> transacaoService.processarPagamento(PagamentoTestHelper.pagamentoValido()))
                .isInstanceOf(TransacaoJaExisteException.class);
    }

    @Test
    void deveValidarRequestNulo() {
        assertThatThrownBy(() -> transacaoService.processarPagamento(null))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarRequestSemObjetoTransacao() {
        PagamentoRequest request = PagamentoRequest.builder().build();

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarIdObrigatorio() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().setId(null);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarCartaoObrigatorio() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().setCartao(null);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarDescricaoObrigatoria() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().setDescricao(null);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarDataHoraObrigatoria() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().getDescricao().setDataHora(null);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarEstabelecimentoObrigatorio() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().getDescricao().setEstabelecimento(null);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarFormaPagamentoObrigatoria() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().setFormaPagamento(null);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarTipoPagamentoObrigatorio() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().getFormaPagamento().setTipo(null);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarParcelasInvalidas() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().getFormaPagamento().setParcelas(0);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarAvistaComUmaParcela() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().getFormaPagamento().setParcelas(2);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarParceladoComMaisDeUmaParcela() {
        PagamentoRequest request = PagamentoTestHelper.pagamentoParcelado();
        request.getTransacao().getFormaPagamento().setParcelas(1);

        assertThatThrownBy(() -> transacaoService.processarPagamento(request))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveBuscarTransacaoPorId() {
        when(transacaoRepository.findById(PagamentoTestHelper.ID_TRANSACAO))
                .thenReturn(Optional.of(PagamentoTestHelper.transacaoAutorizada()));

        PagamentoResponse response = transacaoService.buscarPorId(PagamentoTestHelper.ID_TRANSACAO);

        assertThat(response.getTransacao().getId()).isEqualTo(PagamentoTestHelper.ID_TRANSACAO);
    }

    @Test
    void deveLancarExcecaoQuandoTransacaoNaoExistir() {
        when(transacaoRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacaoService.buscarPorId("inexistente"))
                .isInstanceOf(TransacaoNaoEncontradaException.class);
    }

    @Test
    void deveValidarIdAoBuscar() {
        assertThatThrownBy(() -> transacaoService.buscarPorId(null))
                .isInstanceOf(RequisicaoInvalidaException.class);

        assertThatThrownBy(() -> transacaoService.buscarPorId(" "))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveValidarIdAoEstornar() {
        assertThatThrownBy(() -> transacaoService.estornar(null))
                .isInstanceOf(RequisicaoInvalidaException.class);

        assertThatThrownBy(() -> transacaoService.estornar(" "))
                .isInstanceOf(RequisicaoInvalidaException.class);
    }

    @Test
    void deveListarTransacoes() {
        when(transacaoRepository.findAll()).thenReturn(List.of(PagamentoTestHelper.transacaoAutorizada()));

        List<PagamentoResponse> transacoes = transacaoService.listarTransacoes();

        assertThat(transacoes).hasSize(1);
    }

    @Test
    void deveEstornarTransacaoAutorizada() {
        when(transacaoRepository.findById(PagamentoTestHelper.ID_TRANSACAO))
                .thenReturn(Optional.of(PagamentoTestHelper.transacaoAutorizada()));
        mockSave();

        PagamentoResponse response = transacaoService.estornar(PagamentoTestHelper.ID_TRANSACAO);

        assertThat(response.getTransacao().getDescricao().getStatus()).isEqualTo(StatusTransacao.CANCELADO);
    }

    @Test
    void naoDeveEstornarTransacaoCancelada() {
        when(transacaoRepository.findById(PagamentoTestHelper.ID_TRANSACAO))
                .thenReturn(Optional.of(PagamentoTestHelper.transacaoComStatus(StatusTransacao.CANCELADO)));

        assertThatThrownBy(() -> transacaoService.estornar(PagamentoTestHelper.ID_TRANSACAO))
                .isInstanceOf(EstornoNaoPermitidoException.class);
    }

    @Test
    void naoDeveEstornarTransacaoNegada() {
        when(transacaoRepository.findById(PagamentoTestHelper.ID_TRANSACAO))
                .thenReturn(Optional.of(PagamentoTestHelper.transacaoComStatus(StatusTransacao.NEGADO)));

        assertThatThrownBy(() -> transacaoService.estornar(PagamentoTestHelper.ID_TRANSACAO))
                .isInstanceOf(EstornoNaoPermitidoException.class);
    }

    private void mockSave() {
        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }
}
