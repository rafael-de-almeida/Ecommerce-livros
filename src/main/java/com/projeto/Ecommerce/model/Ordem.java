package com.projeto.Ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ordem")
public class Ordem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ord_id")
    private Long id;

    @Column(name = "ord_preco_total")
    private BigDecimal precoTotal;

    @Column(name = "ord_status")
    private String status;

    @Column(name = "ord_data")
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "cli_id")
    private Clientes cliente;

    @ManyToOne
    @JoinColumn(name = "end_id")
    private Enderecos endereco;

    @OneToMany(mappedBy = "ordem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrdemLivro> livros;
}