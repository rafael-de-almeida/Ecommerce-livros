package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.model.Cartoes;
import com.projeto.Ecommerce.model.Clientes;
import com.projeto.Ecommerce.model.Enderecos;
import com.projeto.Ecommerce.repository.CartaoRepository;
import com.projeto.Ecommerce.repository.ClienteRepository;
import com.projeto.Ecommerce.repository.EnderecoRepository;
import com.projeto.Ecommerce.service.ClientesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin
@RestController
@RequestMapping("/site")
public class SiteController {

    @Autowired
    private ClienteRepository clienterepository;
    @Autowired
    private EnderecoRepository enderecorepository;
    @Autowired
    private CartaoRepository cartaorepository;
    @Autowired
    private ClientesService clienteService;

    @PostMapping("/clientes/post/cliente")
    public ResponseEntity<Clientes> createCliente(@RequestBody Clientes clientes) {
        System.out.println("Recebido: " + clientes);
        if (clientes.getCliNome() == null || clientes.getCliNome().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            Clientes savedClientes = clienterepository.save(clientes);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedClientes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //@PathVariable Integer id
    @PostMapping("/clientes/post/endereco")
    public ResponseEntity<Enderecos> createEndereco(@RequestParam("id") String id, @RequestBody Enderecos enderecos) {
        System.out.println("Recebido: " + enderecos);
        System.out.println("Recebido: " + id);
        if (enderecos.getEndBairro() == null || enderecos.getEndBairro().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {

            Clientes cliente= clienterepository.findById(Integer.valueOf(id)).orElse(null);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            enderecos.setCliente(cliente);
            Enderecos savedEndereco = enderecorepository.save(enderecos);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEndereco);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/clientes/post/cartao")
    public ResponseEntity<Cartoes> createCartao(@RequestParam("id") Integer id, @RequestBody Cartoes cartao) {
        System.out.println("Recebido: " + cartao);
        if (cartao.getCarNome() == null || cartao.getCarNome().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {

            Clientes cliente = clienterepository.findById(id).orElse(null);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            cartao.setCliente(cliente);
            Cartoes savedcartao = cartaorepository.save(cartao);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedcartao);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/clientes/get")
    public List<Clientes> buscarClientes(
            @RequestParam(required = false) String CLI_NOME,
            @RequestParam(required = false) String CLI_GENERO,
            @RequestParam(required = false) String CLI_CPF,
            @RequestParam(required = false) String CLI_EMAIL,
            @RequestParam(required = false) String CLI_TELEFONE,
            @RequestParam(required = false) Integer CLI_IDADE) {

        // Se todos os parâmetros forem nulos, retorna todos os clientes
        if (CLI_NOME == null && CLI_GENERO == null && CLI_CPF == null &&
                CLI_EMAIL == null && CLI_TELEFONE == null && CLI_IDADE == null) {
            return clienteService.buscarTodosClientes();
        }
        return clienteService.buscarClientes(CLI_NOME, CLI_GENERO, CLI_CPF, CLI_EMAIL, CLI_TELEFONE, CLI_IDADE);
    }

    @GetMapping("/clientes/get/{id}")
    public ResponseEntity<Clientes> getClienteById(@PathVariable Integer id) {
        return clienterepository.findById(id)
                .map(cliente -> ResponseEntity.ok().body(cliente))
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/clientes/put")
    public ResponseEntity<Clientes> updateCliente(@RequestParam("id") Integer id, @RequestBody Clientes clientes) {
        // Verifica se o cliente com o ID existe
        Optional<Clientes> clienteData = clienterepository.findById(id);

        if (clienteData.isPresent()) {
            // Atualiza os dados do cliente
            Clientes _clientes = clienteData.get();
            _clientes.setCliNome(clientes.getCliNome());
            _clientes.setCliGenero(clientes.getCliGenero());  // Adicionando a atualização do gênero
            _clientes.setCliNascimento(clientes.getCliNascimento());  // Adicionando a atualização da data de nascimento
            _clientes.setCliIdade(clientes.getCliIdade());  // Adicionando a atualização da idade
            _clientes.setCliCpf(clientes.getCliCpf());  // Adicionando a atualização do CPF
            _clientes.setCliEmail(clientes.getCliEmail());  // Adicionando a atualização do email
            _clientes.setCliTelefone(clientes.getCliTelefone());  // Adicionando a atualização do telefone
            _clientes.setCliStatus(clientes.getCliStatus());
            // Salva o cliente atualizado e retorna a resposta com status OK
            return new ResponseEntity<>(clienterepository.save(_clientes), HttpStatus.OK);
        } else {
            // Se o cliente não for encontrado, retorna resposta de erro NOT_FOUND
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/clientes/delete")
    public void excluirCliente(@RequestParam("id") Integer clienteId) {
        clienterepository.deleteById(clienteId);
    }

    @DeleteMapping("/clientes/deleteall")
    public ResponseEntity<HttpStatus> deleteAllClientes() {
        try {
            clienterepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}