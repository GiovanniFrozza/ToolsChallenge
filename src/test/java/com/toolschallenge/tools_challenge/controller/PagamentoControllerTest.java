package com.toolschallenge.tools_challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolschallenge.tools_challenge.dto.response.PagamentoResponse;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import com.toolschallenge.tools_challenge.exception.EstornoNaoPermitidoException;
import com.toolschallenge.tools_challenge.exception.GlobalExceptionHandler;
import com.toolschallenge.tools_challenge.exception.RequisicaoInvalidaException;
import com.toolschallenge.tools_challenge.exception.TransacaoJaExisteException;
import com.toolschallenge.tools_challenge.exception.TransacaoNaoEncontradaException;
import com.toolschallenge.tools_challenge.mapper.TransacaoMapper;
import com.toolschallenge.tools_challenge.service.TransacaoService;
import com.toolschallenge.tools_challenge.support.PagamentoTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagamentoController.class)
@Import(GlobalExceptionHandler.class)
class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransacaoService transacaoService;

    private final TransacaoMapper transacaoMapper = new TransacaoMapper();

    @Test
    void deveProcessarPagamento() throws Exception {
        PagamentoResponse response = transacaoMapper.toResponse(PagamentoTestHelper.transacaoAutorizada());
        when(transacaoService.processarPagamento(any())).thenReturn(response);

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PagamentoTestHelper.pagamentoValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.id").value(PagamentoTestHelper.ID_TRANSACAO))
                .andExpect(jsonPath("$.transacao.descricao.status").value("AUTORIZADO"));
    }

    @Test
    void deveRetornarConflitoQuandoIdJaExistir() throws Exception {
        when(transacaoService.processarPagamento(any()))
                .thenThrow(new TransacaoJaExisteException(PagamentoTestHelper.ID_TRANSACAO));

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(PagamentoTestHelper.pagamentoValido())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("Já existe uma transação cadastrada com o id: 100023568900001"));
    }

    @Test
    void deveRetornarBadRequestQuandoIdNaoForInformado() throws Exception {
        var request = PagamentoTestHelper.pagamentoValido();
        request.getTransacao().setId(null);

        when(transacaoService.processarPagamento(any()))
                .thenThrow(new RequisicaoInvalidaException("Campo obrigatório: id"));

        mockMvc.perform(post("/pagamentos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Campo obrigatório: id"));
    }

    @Test
    void deveRetornarBadRequestQuandoEstornarTransacaoCancelada() throws Exception {
        when(transacaoService.estornar(PagamentoTestHelper.ID_TRANSACAO))
                .thenThrow(EstornoNaoPermitidoException.transacaoJaCancelada());

        mockMvc.perform(post("/pagamentos/{id}/estorno", PagamentoTestHelper.ID_TRANSACAO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Transação já está cancelada."));
    }

    @Test
    void deveBuscarTransacaoPorId() throws Exception {
        PagamentoResponse response = transacaoMapper.toResponse(PagamentoTestHelper.transacaoAutorizada());
        when(transacaoService.buscarPorId(PagamentoTestHelper.ID_TRANSACAO)).thenReturn(response);

        mockMvc.perform(get("/pagamentos/{id}", PagamentoTestHelper.ID_TRANSACAO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.id").value(PagamentoTestHelper.ID_TRANSACAO));
    }

    @Test
    void deveRetornarNaoEncontradoQuandoIdNaoExistir() throws Exception {
        when(transacaoService.buscarPorId("inexistente"))
                .thenThrow(new TransacaoNaoEncontradaException("inexistente"));

        mockMvc.perform(get("/pagamentos/{id}", "inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveListarTransacoes() throws Exception {
        PagamentoResponse response = transacaoMapper.toResponse(PagamentoTestHelper.transacaoAutorizada());
        when(transacaoService.listarTransacoes()).thenReturn(List.of(response));

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transacao.id").value(PagamentoTestHelper.ID_TRANSACAO));
    }

    @Test
    void deveEstornarTransacao() throws Exception {
        PagamentoResponse response = transacaoMapper.toResponse(
                PagamentoTestHelper.transacaoComStatus(StatusTransacao.CANCELADO));
        when(transacaoService.estornar(PagamentoTestHelper.ID_TRANSACAO)).thenReturn(response);

        mockMvc.perform(post("/pagamentos/{id}/estorno", PagamentoTestHelper.ID_TRANSACAO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.descricao.status").value("CANCELADO"));
    }
}
