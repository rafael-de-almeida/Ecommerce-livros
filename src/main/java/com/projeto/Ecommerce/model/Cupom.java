package com.projeto.Ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Setter
@Getter
@Entity
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private TipoDesconto tipo; // FIXO ou PORCENTAGEM

    private boolean troca; // true = cupom de troca, false = promocional

    private LocalDate validade;

    private Integer usoMaximo = 1;

    private Integer usado = 0;

    @ManyToOne
    private Clientes cliente; // só preenchido se for um cupom de troca

    @ManyToOne
    private Ordem origemTroca; // opcional, usado apenas em cupom de troca

    public enum TipoDesconto {
        FIXO,         // R$10,00 de desconto
        PORCENTAGEM   // 10% de desconto
    }
}

