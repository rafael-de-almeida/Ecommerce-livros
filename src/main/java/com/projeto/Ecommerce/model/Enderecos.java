package com.projeto.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "enderecos")
public class Enderecos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "END_ID")
    private Integer endId;

    @JsonProperty("END_CEP")
    @Column(name = "END_CEP")
    private String endCep;

    @JsonProperty("END_RUA")
    @Column(name = "END_RUA")
    private String endRua;

    @JsonProperty("END_NUMERO")
    @Column(name = "END_NUMERO")
    private String endNumero;

    @JsonProperty("END_COMPLEMENTO")
    @Column(name = "END_COMPLEMENTO")
    private String endComplemento;

    @JsonProperty("END_CIDADE")
    @Column(name = "END_CIDADE")
    private String endCidade;

    @JsonProperty("END_BAIRRO")
    @Column(name = "END_BAIRRO")
    private String endBairro;

    @JsonProperty("END_ESTADO")
    @Column(name = "END_ESTADO")
    private String endEstado;

    @JsonProperty("END_STATUS")
    @Column(name = "END_STATUS")
    private String endStatus;


    @ManyToOne
    @JoinColumn(name = "CLI_ID")
    @JsonBackReference
    private Clientes cliente;

}
