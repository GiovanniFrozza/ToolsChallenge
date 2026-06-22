package com.toolschallenge.tools_challenge.controller;

import com.toolschallenge.tools_challenge.config.openapi.OpenApiExamples;
import com.toolschallenge.tools_challenge.config.openapi.doc.PagamentoApiDoc;
import com.toolschallenge.tools_challenge.dto.request.PagamentoRequest;
import com.toolschallenge.tools_challenge.dto.response.PagamentoResponse;
import com.toolschallenge.tools_challenge.service.TransacaoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Execute os endpoints na ordem numérica (1 → 4) para testar o fluxo completo.")
public class PagamentoController {

    private final TransacaoService transacaoService;

    @PagamentoApiDoc.CriarPagamento
    @PostMapping
    public ResponseEntity<PagamentoResponse> pagar(@RequestBody PagamentoRequest request) {
        PagamentoResponse response = transacaoService.processarPagamento(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getTransacao().getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PagamentoApiDoc.BuscarPagamento
    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> buscarPorId(
            @Parameter(description = "ID de negócio da transação", example = OpenApiExamples.ID_TRANSACAO)
            @PathVariable String id) {
        return ResponseEntity.ok(transacaoService.buscarPorId(id));
    }

    @PagamentoApiDoc.EstornarPagamento
    @PostMapping("/{id}/estorno")
    public ResponseEntity<PagamentoResponse> estornar(
            @Parameter(description = "ID de negócio da transação", example = OpenApiExamples.ID_TRANSACAO)
            @PathVariable String id) {
        return ResponseEntity.ok(transacaoService.estornar(id));
    }

    @PagamentoApiDoc.ListarPagamentos
    @GetMapping
    public ResponseEntity<List<PagamentoResponse>> listar() {
        return ResponseEntity.ok(transacaoService.listarTransacoes());
    }
}
