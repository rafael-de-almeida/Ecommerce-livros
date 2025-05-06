package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrdemLivroResumoDTO {
    private Long ordemLivroId;
    private Long ordemId;
    private Long livroId;
    private String livroTitulo;
    private Integer quantidade;
    private BigDecimal preco;
}
