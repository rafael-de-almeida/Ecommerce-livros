package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class LivroResumoDTO {
    private Integer clienteId;
    private Long ordemId;
    private LocalDate data;
    private Long livroId;
    private String titulo;
    private Integer quantidade;
    private BigDecimal preco;
    private String status;
    private BigDecimal precoTotal;
    private List<String> categorias;


    public LivroResumoDTO(Integer clienteId, Long ordemId, LocalDate data, Long livroId, String titulo,
                          Integer quantidade, BigDecimal preco, String status, BigDecimal precoTotal) {
        this.clienteId = clienteId;
        this.ordemId = ordemId;
        this.data = data;
        this.livroId = livroId;
        this.titulo = titulo;
        this.quantidade = quantidade;
        this.preco = preco;
        this.status = status;
        this.precoTotal = precoTotal;
    }


    public LivroResumoDTO() {
    }
}




