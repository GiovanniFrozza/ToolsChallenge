package com.toolschallenge.tools_challenge.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toolschallenge.tools_challenge.enums.StatusTransacao;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Descricao {

    @Column(nullable = false, scale = 2)
    private BigDecimal valor;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String estabelecimento;

    private String nsu;

    private String codigoAutorizacao;

    @Enumerated(EnumType.STRING)
    private StatusTransacao status;
}
