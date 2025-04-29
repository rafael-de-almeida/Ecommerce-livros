package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueDTO {
    private Integer id;
    private Integer idLivro;
    private String tituloLivro;
    private LocalDate dataEntrada;
    private String fornecedor;
    private Integer quantidade;
    private BigDecimal valorDeCusto;

}
