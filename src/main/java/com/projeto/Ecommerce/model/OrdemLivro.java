// OrdemLivro.java
package com.projeto.Ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ordem_livros")
public class OrdemLivro {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ord_liv_id")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "ord_id")
        private Ordem ordem;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "liv_id")
        private Livros livro;

        @Column(name = "ord_liv_qtd")
        private Integer quantidade;

        @Column(name = "ord_liv_pre")
        private BigDecimal preco;
}
