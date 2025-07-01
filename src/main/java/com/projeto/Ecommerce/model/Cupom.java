package com.projeto.Ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
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
    @Column(nullable = false)
    private Integer usado = 0;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Clientes cliente; // só preenchido se for um cupom de troca

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "origem_troca_ord_id")
    private Ordem origemTroca; // opcional, usado apenas em cupom de troca

    public enum TipoDesconto {
        TROCA,
        PROMOCIONAL,
    }

    public boolean isValido() {
        return (this.usado < this.usoMaximo) && (this.validade == null || !this.validade.isBefore(LocalDate.now()));
    }

}

