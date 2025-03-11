package com.projeto.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cartoes")
public class Cartoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAR_ID")
    private Integer carId;

    @JsonProperty("CAR_NUMERO")
    @Column(name = "CAR_NUMERO")
    private String carNumero;

    @JsonProperty("CAR_BANDEIRA")
    @Column(name = "CAR_BANDEIRA")
    private String carBandeira;

    @JsonProperty("CAR_CODIGO")
    @Column(name = "CAR_CODIGO")
    private String carCodigo;

    @JsonProperty("CAR_NOME")
    @Column(name = "CAR_NOME")
    private String carNome;

    @JsonProperty("CAR_VENCIMENTO")
    @Column(name = "CAR_VENCIMENTO")
    private Date carVencimento;

    @JsonProperty("CAR_STATUS")
    @Column(name = "CAR_STATUS")
    private String carStatus;

    @ManyToOne
    @JoinColumn(name = "CLI_ID")
    @JsonBackReference  // Não serializa a referência de volta para o cliente
    private Clientes cliente;
}
