package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalizarCompraRequest {
    private Integer clienteId;
    private Double valorTotal;
    private Long cupomId;
}
