package com.projeto.Ecommerce.dto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrdemResumoDTO {
    private Long numeroPedido;
    private String nomeCliente;
    private List<String> livros;
    private List<String> categorias;
    private BigDecimal valorTotal;
    private String status;
    private LocalDate data;
}
