package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrdemRequestDTO {

    private BigDecimal precoTotal;
    private String status;
    private LocalDate data;
    private Integer clienteId;
    private Integer enderecoId;
    private List<OrdemLivroDTO> livros;
    private List<PagamentoDTO> pagamentos;
    private List<String> codigosCupons;

}
