package com.toolschallenge.tools_challenge.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toolschallenge.tools_challenge.dto.request.PagamentoRequest;
import com.toolschallenge.tools_challenge.support.PagamentoTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PagamentoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveExecutarFluxoCompletoDePagamento() throws Exception {
        enviarPagamento(PagamentoTestHelper.pagamentoValido())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.descricao.status").value("AUTORIZADO"));

        mockMvc.perform(get("/pagamentos/{id}", PagamentoTestHelper.ID_TRANSACAO))
                .andExpect(status().isOk());

        mockMvc.perform(post("/pagamentos/{id}/estorno", PagamentoTestHelper.ID_TRANSACAO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.descricao.status").value("CANCELADO"));

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transacao.id").value(PagamentoTestHelper.ID_TRANSACAO));
    }

    @Test
    void deveRetornarConflitoParaIdDuplicado() throws Exception {
        enviarPagamento(PagamentoTestHelper.pagamentoValido())
                .andExpect(status().isOk());

        enviarPagamento(PagamentoTestHelper.pagamentoValido())
                .andExpect(status().isConflict());
    }

    @Test
    void deveNegarPagamentoComValorInvalido() throws Exception {
        enviarPagamento(PagamentoTestHelper.pagamentoComValor(BigDecimal.ZERO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transacao.descricao.status").value("NEGADO"));
    }

    private ResultActions enviarPagamento(PagamentoRequest request) throws Exception {
        return mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }
}
