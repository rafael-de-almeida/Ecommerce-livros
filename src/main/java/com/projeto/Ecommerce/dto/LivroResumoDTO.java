package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class LivroResumoDTO {
    private String titulo;
    private Integer quantidade;
    private BigDecimal preco;
}
