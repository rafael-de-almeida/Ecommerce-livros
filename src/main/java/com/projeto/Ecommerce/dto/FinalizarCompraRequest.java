package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FinalizarCompraRequest {
    private Integer clienteId;
    private Double valorTotal;
    private List<Long> cuponsIds;

}
