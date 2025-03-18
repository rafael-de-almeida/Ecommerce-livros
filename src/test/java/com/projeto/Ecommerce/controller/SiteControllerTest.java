package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.model.Clientes;
import com.projeto.Ecommerce.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import java.time.LocalDate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SiteControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository clienteRepository;


    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/site/clientes/";
    }

    @Test
    void testCriarCliente() {
        Clientes novoCliente = new Clientes();
        novoCliente.setCliNome("João Silva");
        novoCliente.setCliCpf("123.456.789-00");
        novoCliente.setCliGenero("Masculino");
        novoCliente.setCliEmail("joao@email.com");
        novoCliente.setCliSenha("123");
        novoCliente.setCliTelefone("11999999999");
        novoCliente.setCliStatus("Ativo");
        novoCliente.setCliIdade(20);
        novoCliente.setCliNascimento(LocalDate.of(2005, 10, 1));
        ResponseEntity<Clientes> response = restTemplate.postForEntity(baseUrl + "post/cliente", novoCliente, Clientes.class);



        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCliNome()).isEqualTo("João Silva");
        assertThat(response.getBody().getCliCpf()).isEqualTo("123.456.789-00");
        assertThat(response.getBody().getCliEmail()).isEqualTo("joao@email.com");
        assertThat(response.getBody().getCliTelefone()).isEqualTo("11999999999");
        assertThat(response.getBody().getCliStatus()).isEqualTo("Ativo");
        assertThat(response.getBody().getCliIdade()).isEqualTo(20);
        assertThat(response.getBody().getCliNascimento()).isEqualTo(LocalDate.of(2005, 10, 1));
        assertThat(response.getBody().getCliSenha()).isEqualTo("123");
        assertThat(response.getBody().getCliGenero()).isEqualTo("Masculino");
    }

    @Test
    void testBuscarClientePorId() {
        Clientes novoCliente = new Clientes();
        novoCliente.setCliNome("João Silva");
        novoCliente.setCliCpf("123.456.789-00");
        novoCliente.setCliGenero("Masculino");
        novoCliente.setCliEmail("joao@email.com");
        novoCliente.setCliSenha("123");
        novoCliente.setCliTelefone("11999999999");
        novoCliente.setCliStatus("Ativo");
        novoCliente.setCliIdade(20);
        novoCliente.setCliNascimento(LocalDate.of(2005, 10, 1));

        novoCliente = clienteRepository.save(novoCliente);

        ResponseEntity<Clientes> response = restTemplate.getForEntity(baseUrl + "/get/" + novoCliente.getCliId(), Clientes.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCliNome()).isEqualTo("Maria Souza");
    }

    @Test
    void testAtualizarCliente() {
        Clientes novoCliente = new Clientes();
        novoCliente.setCliNome("João Silva");
        novoCliente.setCliCpf("123.456.789-00");
        novoCliente.setCliGenero("Masculino");
        novoCliente.setCliEmail("joao@email.com");
        novoCliente.setCliSenha("123");
        novoCliente.setCliTelefone("11999999999");
        novoCliente.setCliStatus("Ativo");
        novoCliente.setCliIdade(20);
        novoCliente.setCliNascimento(LocalDate.of(2005, 10, 1));

        novoCliente = clienteRepository.save(novoCliente);

        novoCliente.setCliNome("Carla Oliveira");
        novoCliente.setCliCpf("321.456.789-00");
        novoCliente.setCliGenero("Feminino");
        novoCliente.setCliEmail("carla@email.com");
        novoCliente.setCliSenha("321");
        novoCliente.setCliIdade(30);
        novoCliente.setCliNascimento(LocalDate.of(1990, 5, 5));
        novoCliente.setCliTelefone("128833737");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Clientes> requestEntity = new HttpEntity<>(novoCliente, headers);

        ResponseEntity<Clientes> response = restTemplate.exchange(baseUrl + "/put?id=" + novoCliente.getCliId(),
                HttpMethod.PUT, requestEntity, Clientes.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCliNome()).isEqualTo("Carla Oliveira");
        assertThat(response.getBody().getCliCpf()).isEqualTo("321.456.789-00");
        assertThat(response.getBody().getCliGenero()).isEqualTo("Feminino");
        assertThat(response.getBody().getCliEmail()).isEqualTo("carla@email.com");
        assertThat(response.getBody().getCliIdade()).isEqualTo(30);
        assertThat(response.getBody().getCliNascimento()).isEqualTo(LocalDate.of(1990, 5, 5));
        assertThat(response.getBody().getCliTelefone()).isEqualTo("128833737");
        assertThat(response.getBody().getCliStatus()).isEqualTo("Ativo");
    }

    @Test
    void testExcluirCliente() {
        Clientes novoCliente = new Clientes();
        novoCliente.setCliNome("João Silva");
        novoCliente.setCliCpf("123.456.789-00");
        novoCliente.setCliGenero("Masculino");
        novoCliente.setCliEmail("joao@email.com");
        novoCliente.setCliSenha("123");
        novoCliente.setCliTelefone("11999999999");
        novoCliente.setCliStatus("Ativo");
        novoCliente.setCliIdade(20);
        novoCliente.setCliNascimento(LocalDate.of(2005, 10, 1));

        novoCliente = clienteRepository.save(novoCliente);

        restTemplate.delete(baseUrl + "/delete?id=" + novoCliente.getCliId());

        ResponseEntity<Clientes> response = restTemplate.getForEntity(baseUrl + "/get/" + novoCliente.getCliId(), Clientes.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}