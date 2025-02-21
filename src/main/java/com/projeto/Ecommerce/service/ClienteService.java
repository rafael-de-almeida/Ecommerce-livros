package com.projeto.Ecommerce.service;
import com.projeto.Ecommerce.model.Cliente;
import com.projeto.Ecommerce.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ClienteService {


        @Autowired
        private ClienteRepository clienteRepository;

        public Cliente cliente(String nome) {
            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            // Using the class name as the sequence name
            return clienteRepository.save(cliente);
        }
}
