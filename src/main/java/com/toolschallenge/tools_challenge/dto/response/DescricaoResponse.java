package com.toolschallenge.tools_challenge.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescricaoResponse {

    private BigDecimal valor;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHora;

    private String estabelecimento;
    private String nsu;
    private String codigoAutorizacao;
    private StatusTransacao status;
}
