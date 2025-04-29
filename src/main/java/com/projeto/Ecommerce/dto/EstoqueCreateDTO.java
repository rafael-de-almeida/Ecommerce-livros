package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class EstoqueCreateDTO {
    private Integer idLivro;
    private LocalDate dataEntrada;
    private String fornecedor;
    private Integer quantidade;
    private BigDecimal valorDeCusto;

    // Getters e Setters
}