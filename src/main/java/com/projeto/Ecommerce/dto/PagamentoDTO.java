package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PagamentoDTO {
    private Integer cartaoId;
    private String status;
    private BigDecimal valor;
}
