package com.toolschallenge.tools_challenge.config.openapi.doc;

import com.toolschallenge.tools_challenge.config.openapi.OpenApiExamples;
import com.toolschallenge.tools_challenge.dto.request.PagamentoRequest;
import com.toolschallenge.tools_challenge.dto.response.PagamentoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class PagamentoApiDoc {

    private PagamentoApiDoc() {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "1. Realizar pagamento",
            description = "Passo 1: cria a transação. Use um id novo a cada teste.",
            requestBody = @RequestBody(
                    description = "Dados do pagamento",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagamentoRequest.class),
                            examples = @ExampleObject(name = "Pagamento", value = OpenApiExamples.PAGAMENTO_REQUEST)
                    )
            )
    )
    @ApiResponse(
            responseCode = "201",
            description = "Pagamento processado e transação criada",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagamentoResponse.class),
                    examples = @ExampleObject(name = "Pagamento autorizado", value = OpenApiExamples.PAGAMENTO_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "409", description = "Transação com o mesmo id já cadastrada")
    public @interface CriarPagamento {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "4. Listar transações", description = "Passo 4: lista todas as transações cadastradas.")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de transações",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "Lista", value = OpenApiExamples.LISTA_RESPONSE)
            )
    )
    public @interface ListarPagamentos {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "2. Buscar transação por ID", description = "Passo 2: consulta a transação criada no passo 1.")
    @ApiResponse(
            responseCode = "200",
            description = "Transação encontrada",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "Transação", value = OpenApiExamples.PAGAMENTO_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    public @interface BuscarPagamento {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "3. Estornar transação", description = "Passo 3: estorna a transação autorizada do passo 1.")
    @ApiResponse(
            responseCode = "200",
            description = "Estorno realizado",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(name = "Estorno", value = OpenApiExamples.ESTORNO_RESPONSE)
            )
    )
    @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    @ApiResponse(responseCode = "400", description = "Estorno não permitido")
    public @interface EstornarPagamento {
    }
}
