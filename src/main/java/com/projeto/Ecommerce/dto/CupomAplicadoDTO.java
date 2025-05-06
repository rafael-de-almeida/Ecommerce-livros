package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CupomAplicadoDTO {
    private boolean sucesso;
    private String mensagem;
    private double valorOriginal;
    private double valorDesconto;
    private double valorFinal;
    private Long cupomId;
}
