package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CupomTrocaRequestDTO {
    private Integer clienteId;
    private BigDecimal valorEmCentavos; // Armazene o valor em centavos para evitar problemas de arredondamento
    private Long idOrdemOrigem;
}

