package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.model.Cliente;
import com.projeto.Ecommerce.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/site")
public class SiteController {

    @Autowired
    ClienteRepository clienterepository;

    @PostMapping("/clientes/post")
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente savedCliente = clienterepository.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCliente);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception if necessary
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/clientes/get")
    public ResponseEntity<List<Cliente>> getAllClientes(@RequestParam(required = false) String id) {
        try {
            List<Cliente> clientes = new ArrayList<Cliente>();

            if (id == null)
                clienterepository.findAll().forEach(clientes::add);
            if (clientes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/clientes/get/{id}")
    public ResponseEntity<Cliente> getClientelById(@PathVariable("id") Long id) {
        Optional<Cliente> clienteData = clienterepository.findById(id);

        if (clienteData.isPresent()) {
            return new ResponseEntity<>(clienteData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/clientes/put/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable("id")  Long id, @RequestBody Cliente cliente) {
        Optional<Cliente> clienteData = clienterepository.findById(id);

        if (clienteData.isPresent()) {
            Cliente _cliente = clienteData.get();
            _cliente.setName(cliente.getName());
            return new ResponseEntity<>(clienterepository.save(_cliente), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/clientes/delete/{id}")
    public ResponseEntity<HttpStatus> deleteCliente(@PathVariable("id") Long id) {
        try {
            Optional<Cliente> clienteData = clienterepository.findById(id);
            if (clienteData.isPresent()) {
                clienterepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/clientes/delete")
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