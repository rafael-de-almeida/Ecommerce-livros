package com.projeto.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "livros")
public class Livros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIV_ID")
    private Integer livId;

    @JsonProperty("LIV_AUTOR")
    @Column(name = "LIV_AUTOR")
    private String livAutor;

    @JsonProperty
    @Column(name = "LIV_ANO")
    private LocalDate livAno;


    @Column(name = "LIV_TITULO")
    private String livTitulo;

    @JsonProperty("LIV_EDITORA")
    @Column(name = "LIV_EDITORA")
    private String livEditora;

    @JsonProperty("LIV_EDICAO")
    @Column(name = "LIV_EDICAO")
    private String livEdicao;

    @JsonProperty("LIV_ISBN")
    @Column(name = "LIV_ISBN")
    private String livIsbn;

    @JsonProperty("LIV_QTD_PAGINAS")
    @Column(name = "LIV_QTD_PAGINAS")
    private int livQtdPaginas;

    @JsonProperty("LIV_SINOPSE")
    @Column(name = "LIV_SINOPSE", columnDefinition = "TEXT")
    private String livSinopse;

    @JsonProperty("LIV_VENDA")
    @Column(name = "LIV_VENDA")
    private String livVenda;

    @JsonProperty("LIV_DIMENSAO")
    @Column(name = "LIV_DIMENSAO")
    private String livDimensao;

    @Column(name = "LIV_IMAGEM")
    private String livImagem;

    @Column (name = "LIV_CATEGORIA")
    private String livCategoria;

    @Column (name = "LIV_COD_BARRAS")
    private String liv_cod_barras;
}
