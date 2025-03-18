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
        baseUrl = "http://localhost:" + port + "/site/clientes";
    }

    @Test
    void testCriarCliente() {
        Clientes novoCliente = new Clientes();
        novoCliente.setCliNome("João Silva");
        novoCliente.setCliCpf("123.456.789-00");
        novoCliente.setCliEmail("joao@email.com");
        novoCliente.setCliTelefone("11999999999");
        novoCliente.setCliStatus("Ativo");

        ResponseEntity<Clientes> response = restTemplate.postForEntity(baseUrl + "/post/cliente", novoCliente, Clientes.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCliNome()).isEqualTo("João Silva");
        assertThat(response.getBody().getCliCpf()).isEqualTo("123.456.789-00");
        assertThat(response.getBody().getCliEmail()).isEqualTo("joao@email.com");
        assertThat(response.getBody().getCliTelefone()).isEqualTo("11999999999");
        assertThat(response.getBody().getCliStatus()).isEqualTo("Ativo");
    }

    @Test
    void testBuscarClientePorId() {
        Clientes cliente = new Clientes();
        cliente.setCliNome("Maria Souza");
        cliente.setCliCpf("987.654.321-00");
        cliente.setCliEmail("maria@email.com");
        cliente.setCliTelefone("11988888888");
        cliente.setCliStatus("Ativo");
        cliente.setCliNascimento(LocalDate.of(2005, 10, 1));

        cliente = clienteRepository.save(cliente);

        ResponseEntity<Clientes> response = restTemplate.getForEntity(baseUrl + "/get/" + cliente.getCliId(), Clientes.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCliNome()).isEqualTo("Maria Souza");
    }

    @Test
    void testAtualizarCliente() {
        Clientes cliente = new Clientes();
        cliente.setCliNome("Carlos Oliveira");
        cliente.setCliCpf("111.222.333-44");
        cliente.setCliEmail("carlos@email.com");
        cliente.setCliTelefone("11977777777");
        cliente.setCliStatus("ativo");
        cliente.setCliNascimento(LocalDate.of(2000, 5, 2));


        cliente = clienteRepository.save(cliente);

        cliente.setCliNome("Carlos Oliveira Atualizado");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Clientes> requestEntity = new HttpEntity<>(cliente, headers);

        ResponseEntity<Clientes> response = restTemplate.exchange(baseUrl + "/put?id=" + cliente.getCliId(),
                HttpMethod.PUT, requestEntity, Clientes.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCliNome()).isEqualTo("Carlos Oliveira Atualizado");
    }

    @Test
    void testExcluirCliente() {
        Clientes cliente = new Clientes();
        cliente.setCliNome("Pedro Santos");
        cliente.setCliCpf("555.666.777-88");
        cliente.setCliEmail("pedro@email.com");
        cliente.setCliTelefone("11966666666");
        cliente.setCliStatus("ativo");
        cliente.setCliNascimento(LocalDate.of(2005, 10, 1));

        cliente = clienteRepository.save(cliente);

        restTemplate.delete(baseUrl + "/delete?id=" + cliente.getCliId());

        ResponseEntity<Clientes> response = restTemplate.getForEntity(baseUrl + "/get/" + cliente.getCliId(), Clientes.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}