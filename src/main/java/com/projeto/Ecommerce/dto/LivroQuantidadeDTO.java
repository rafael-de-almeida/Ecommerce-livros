package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class LivroQuantidadeDTO {
    private String titulo;
    private int quantidade;
    private List<String> categorias;

    public LivroQuantidadeDTO(String titulo, int quantidade, List<String> categorias) {
        this.titulo = titulo;
        this.quantidade = quantidade;
        this.categorias = categorias;
    }
}