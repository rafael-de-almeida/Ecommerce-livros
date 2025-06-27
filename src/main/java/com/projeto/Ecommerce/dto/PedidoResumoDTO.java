package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PedidoResumoDTO {
    private Long id;
    private LocalDate data;
    private String status;
    private List<LivroQuantidadeDTO> livros;
}
