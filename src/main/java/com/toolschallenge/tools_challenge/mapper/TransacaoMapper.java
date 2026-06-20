package com.toolschallenge.tools_challenge.mapper;

import com.toolschallenge.tools_challenge.dto.request.TransacaoRequest;
import com.toolschallenge.tools_challenge.dto.response.DescricaoResponse;
import com.toolschallenge.tools_challenge.dto.response.FormaPagamentoResponse;
import com.toolschallenge.tools_challenge.dto.response.PagamentoResponse;
import com.toolschallenge.tools_challenge.dto.response.TransacaoResponse;
import com.toolschallenge.tools_challenge.entity.Descricao;
import com.toolschallenge.tools_challenge.entity.FormaPagamento;
import com.toolschallenge.tools_challenge.entity.Transacao;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import org.springframework.stereotype.Component;

@Component
public class TransacaoMapper {

    public PagamentoResponse toResponse(Transacao transacao) {
        Descricao descricao = transacao.getDescricao();
        FormaPagamento formaPagamento = transacao.getFormaPagamento();

        return PagamentoResponse.builder()
                .transacao(TransacaoResponse.builder()
                        .cartao(transacao.getCartao())
                        .id(transacao.getId())
                        .descricao(DescricaoResponse.builder()
                                .valor(descricao.getValor())
                                .dataHora(descricao.getDataHora())
                                .estabelecimento(descricao.getEstabelecimento())
                                .nsu(descricao.getNsu())
                                .codigoAutorizacao(descricao.getCodigoAutorizacao())
                                .status(descricao.getStatus())
                                .build())
                        .formaPagamento(FormaPagamentoResponse.builder()
                                .tipo(formaPagamento.getTipo())
                                .parcelas(formaPagamento.getParcelas())
                                .build())
                        .build())
                .build();
    }

    public Transacao toEntity(TransacaoRequest dados, StatusTransacao status, String nsu, String codigoAutorizacao) {
        Descricao descricao = Descricao.builder()
                .valor(dados.getDescricao().getValor())
                .dataHora(dados.getDescricao().getDataHora())
                .estabelecimento(dados.getDescricao().getEstabelecimento())
                .status(status)
                .nsu(nsu)
                .codigoAutorizacao(codigoAutorizacao)
            .build();

        return Transacao.builder()
                .id(dados.getId())
                .cartao(dados.getCartao())
                .descricao(descricao)
                .formaPagamento(FormaPagamento.builder()
                        .tipo(dados.getFormaPagamento().getTipo())
                        .parcelas(dados.getFormaPagamento().getParcelas())
                        .build())
                .build();
    }
}
