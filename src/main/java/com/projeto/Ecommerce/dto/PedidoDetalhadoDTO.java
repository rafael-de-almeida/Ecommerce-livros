package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class PedidoDetalhadoDTO {

    private Long ordemId;
    private LocalDate data;
    private BigDecimal valorTotal;
    private String status;
    private List<LivroResumoDTO> livros;

    public PedidoDetalhadoDTO(Long ordemId, LocalDate data, BigDecimal valorTotal, String status) {
        this.ordemId = ordemId;
        this.data = data;
        this.valorTotal = valorTotal;
        this.status = status;
    }
}
