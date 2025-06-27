package com.projeto.Ecommerce.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class LivroQuantidadeDTO {
    private String titulo;
    private int quantidade;
    private List<String> categorias;
}
