package com.projeto.Ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pagamento_seq")
    @SequenceGenerator(name = "pagamento_seq", sequenceName = "pagamento_pag_id_seq", allocationSize = 1)
    @Column(name = "pag_id")
    private Integer id;

    @Column(name = "pag_status", nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ord_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Ordem ordem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Cartoes cartao;
}
