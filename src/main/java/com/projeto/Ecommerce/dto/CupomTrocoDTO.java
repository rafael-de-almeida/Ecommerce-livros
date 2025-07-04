package com.projeto.Ecommerce.dto;

import java.math.BigDecimal;

public class CupomTrocoDTO {

    private Integer clienteId;
    private BigDecimal valor;

    // Getters e Setters
    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}