package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CupomTrocaRequestDTO {
    private Integer clienteId;
    private BigDecimal valorEmCentavos;
    private Long idOrdemOrigem;
}

