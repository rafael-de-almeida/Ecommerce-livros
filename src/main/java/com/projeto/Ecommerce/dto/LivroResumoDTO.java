package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class LivroResumoDTO {
    private Integer clienteId;
    private Long id;
    private LocalDate data;
    private Long livroId;
    private String titulo;
    private Integer quantidade;
    private BigDecimal preco;
    private String status;
    private BigDecimal precoTotal;
}

