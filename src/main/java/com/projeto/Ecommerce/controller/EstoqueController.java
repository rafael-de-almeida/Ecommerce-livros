package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.dto.EstoqueCreateDTO;
import com.projeto.Ecommerce.dto.EstoqueDTO;
import com.projeto.Ecommerce.service.EstoqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/site")
public class EstoqueController {

    EstoqueService estoqueService;

@GetMapping("/estoque/get")
    public List<EstoqueDTO> listarTodos() {
        return estoqueService.listarTodos();
    }

    @GetMapping("/{id}")
    public EstoqueDTO buscarPorId(@PathVariable Long id) {
        return estoqueService.buscarPorId(id);
    }

    @PostMapping("/estoque/put")
    public ResponseEntity<EstoqueDTO> adicionar(@RequestBody EstoqueCreateDTO estoqueCreateDTO) {
        EstoqueDTO novo = estoqueService.adicionar(estoqueCreateDTO);
        return new ResponseEntity<>(novo, HttpStatus.CREATED);
    }

    @PatchMapping("/estoque/entrada/{id}")
    public EstoqueDTO entradaEstoque(@PathVariable Long id, @RequestParam int quantidade) {
        return estoqueService.entradaEstoque(id, quantidade);
    }

    @PatchMapping("/estoque/saida/{id}")
    public EstoqueDTO saidaEstoque(@PathVariable Long id, @RequestParam int quantidade) {
        return estoqueService.saidaEstoque(id, quantidade);

    }
}
