package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.dto.OrdemLivroResumoDTO;
import com.projeto.Ecommerce.model.OrdemLivro;
import com.projeto.Ecommerce.service.OrdemLivrosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

    @RestController
    @RequestMapping("/site")
    public class OrdemLivrosController {

        @Autowired
        private OrdemLivrosService ordemLivroService;

        @GetMapping("/ordens-livros/{ordId}")
        public ResponseEntity<List<OrdemLivroResumoDTO>> getOrdemLivrosByOrdemId(@PathVariable("ordId") Long ordId) {
            List<OrdemLivroResumoDTO> ordemLivros = ordemLivroService.findAllByOrdemId(ordId);
            if (ordemLivros.isEmpty()) {
                return ResponseEntity.noContent().build(); // Retorna 204 se não houver resultados
            }
            return ResponseEntity.ok(ordemLivros); // Retorna 200 com os dados
        }
    }
