package com.toolschallenge.tools_challenge.service;

import com.toolschallenge.tools_challenge.dto.request.PagamentoRequest;
import com.toolschallenge.tools_challenge.dto.request.TransacaoRequest;
import com.toolschallenge.tools_challenge.dto.response.PagamentoResponse;
import com.toolschallenge.tools_challenge.entity.Transacao;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import com.toolschallenge.tools_challenge.enums.TipoPagamento;
import com.toolschallenge.tools_challenge.exception.EstornoNaoPermitidoException;
import com.toolschallenge.tools_challenge.exception.RequisicaoInvalidaException;
import com.toolschallenge.tools_challenge.exception.TransacaoJaExisteException;
import com.toolschallenge.tools_challenge.exception.TransacaoNaoEncontradaException;
import com.toolschallenge.tools_challenge.mapper.TransacaoMapper;
import com.toolschallenge.tools_challenge.repository.TransacaoRepository;
import com.toolschallenge.tools_challenge.util.CodigoTransacaoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;

    @Transactional
    public PagamentoResponse processarPagamento(PagamentoRequest request) {
        validarPagamentoRequest(request);

        TransacaoRequest dadosTransacao = request.getTransacao();

        if (transacaoRepository.existsById(dadosTransacao.getId())) {
            throw new TransacaoJaExisteException(dadosTransacao.getId());
        }

        StatusTransacao status = definirStatusPagamento(dadosTransacao);
        String nsu = null;
        String codigoAutorizacao = null;

        if (status == StatusTransacao.AUTORIZADO) {
            nsu = CodigoTransacaoUtil.gerarNsu();
            codigoAutorizacao = CodigoTransacaoUtil.gerarCodigoAutorizacao();
        }

        Transacao transacao = transacaoMapper.toEntity(dadosTransacao, status, nsu, codigoAutorizacao);
        return transacaoMapper.toResponse(transacaoRepository.save(transacao));
    }

    @Transactional
    public List<PagamentoResponse> listarTransacoes() {
        return transacaoRepository.findAll().stream()
                .map(transacaoMapper::toResponse)
            .toList();
    }

    @Transactional
    public PagamentoResponse buscarPorId(String id) {
        validarId(id);

        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException(id));
        return transacaoMapper.toResponse(transacao);
    }

    @Transactional
    public PagamentoResponse estornar(String id) {
        validarId(id);

        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException(id));

        StatusTransacao statusAtual = transacao.getDescricao().getStatus();

        if (statusAtual == StatusTransacao.CANCELADO) {
            throw EstornoNaoPermitidoException.transacaoJaCancelada();
        }

        if (statusAtual == StatusTransacao.NEGADO) {
            throw EstornoNaoPermitidoException.transacaoNegada();
        }

        transacao.getDescricao().setStatus(StatusTransacao.CANCELADO);
        return transacaoMapper.toResponse(transacaoRepository.save(transacao));
    }

    private StatusTransacao definirStatusPagamento(TransacaoRequest dados) {
        BigDecimal valor = dados.getDescricao().getValor();
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return StatusTransacao.NEGADO;
        }

        return StatusTransacao.AUTORIZADO;
    }

    private void validarPagamentoRequest(PagamentoRequest request) {
        if (request == null || request.getTransacao() == null) {
            throw new RequisicaoInvalidaException("Campo obrigatório: transacao");
        }

        TransacaoRequest dados = request.getTransacao();

        validarId(dados.getId());

        if (dados.getCartao() == null || dados.getCartao().isBlank()) {
            throw new RequisicaoInvalidaException("Campo obrigatório: cartao");
        }

        if (dados.getDescricao() == null) {
            throw new RequisicaoInvalidaException("Campo obrigatório: descricao");
        }

        if (dados.getDescricao().getDataHora() == null) {
            throw new RequisicaoInvalidaException("Campo obrigatório: dataHora");
        }

        if (dados.getDescricao().getEstabelecimento() == null) {
            throw new RequisicaoInvalidaException("Campo obrigatório: estabelecimento");
        }

        if (dados.getFormaPagamento() == null) {
            throw new RequisicaoInvalidaException("Campo obrigatório: formaPagamento");
        }

        if (dados.getFormaPagamento().getTipo() == null) {
            throw new RequisicaoInvalidaException("Campo obrigatório: tipo");
        }

        if (dados.getFormaPagamento().getParcelas() == null) {
            throw new RequisicaoInvalidaException("Campo obrigatório: parcelas");
        }

        if (dados.getFormaPagamento().getParcelas() < 1) {
            throw new RequisicaoInvalidaException("O número de parcelas deve ser maior ou igual a 1.");
        }

        validarTipoParcelas(dados.getFormaPagamento().getTipo(), dados.getFormaPagamento().getParcelas());
    }

    private void validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new RequisicaoInvalidaException("Campo obrigatório: id");
        }
    }

    private void validarTipoParcelas(TipoPagamento tipo, Integer parcelas) {
        if (tipo == TipoPagamento.AVISTA && parcelas != 1) {
            throw new RequisicaoInvalidaException("Pagamento à vista deve ter exatamente 1 parcela.");
        }

        if (tipo != TipoPagamento.AVISTA && parcelas <= 1) {
            throw new RequisicaoInvalidaException("Pagamento parcelado deve ter mais de 1 parcela.");
        }
    }
}
