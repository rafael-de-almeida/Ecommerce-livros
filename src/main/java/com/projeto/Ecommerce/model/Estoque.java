package com.projeto.Ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estoque")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_livro", nullable = false)
    private Livros livro;

    @Column(name = "data_entrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "fornecedor")
    private String fornecedor;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "valor_de_custo", precision = 10, scale = 2)
    private BigDecimal valorDeCusto;

}
