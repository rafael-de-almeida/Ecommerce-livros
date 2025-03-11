package com.projeto.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Clientes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLI_ID")
    private Integer cliId;

    @JsonProperty("CLI_NOME")
    @Column(name = "CLI_NOME", nullable = false)
    private String cliNome;

    @JsonProperty("CLI_NASCIMENTO")
    @Column(name = "CLI_NASCIMENTO")
    private Date cliNascimento;

    @JsonProperty("CLI_GENERO")
    @Column(name = "CLI_GENERO")
    private String cliGenero;

    @JsonProperty("CLI_IDADE")
    @Column(name = "CLI_IDADE")
    private int cliIdade;

    @JsonProperty("CLI_SENHA")
    @Column(name = "CLI_SENHA")
    private String cliSenha;

    @JsonProperty("CLI_CPF")
    @Column(name = "CLI_CPF")
    private String cliCpf;

    @JsonProperty("CLI_EMAIL")
    @Column(name = "CLI_EMAIL")
    private String cliEmail;

    @JsonProperty("CLI_TELEFONE")
    @Column(name = "CLI_TELEFONE")
    private String cliTelefone;

    @JsonProperty("CLI_STATUS")
    @Column(name = "CLI_STATUS")
    private String cliStatus;
}
