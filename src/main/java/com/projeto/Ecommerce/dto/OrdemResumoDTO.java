package com.projeto.Ecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrdemResumoDTO {
    private Long numeroPedido;
    private String nomeCliente;
    private List<LivroQuantidadeDTO> livros;
    private List<String> categorias;
    private BigDecimal valorTotal;
    private String status;
    private LocalDate data; // Adicione isso
}
