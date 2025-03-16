package com.projeto.Ecommerce.service;
import com.projeto.Ecommerce.model.Clientes;
import com.projeto.Ecommerce.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ClientesService {

    @Autowired
    private ClienteRepository clientesRepository;

    public List<Clientes> buscarClientes(String nome, String genero, String cpf, String email, String telefone, Integer idade) {
        return clientesRepository.findByFilters(nome, genero, cpf, email, telefone, idade);
    }
    public List<Clientes> buscarTodosClientes() {
        return clientesRepository.findAll();
    }
    public Integer getLastClient() {
        Clientes lastClient = clientesRepository.findTopByOrderByCliIdDesc();
        return (lastClient != null) ? lastClient.getCliId() : null;
    }
}
