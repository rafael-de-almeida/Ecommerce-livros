package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.model.Livros;
import com.projeto.Ecommerce.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/site")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping("/livros")
    public ResponseEntity<List<Livros>> getLivrosByFiltro(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String editora,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer qtdpaginas,
            @RequestParam(required = false) String codbarras) {
        List<Livros> livros = livroRepository.findLivrosBy(autor, categoria, ano, titulo, editora, isbn, qtdpaginas, codbarras);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/livros/{id}")
    public ResponseEntity<Livros> getLivroPorId(@PathVariable Integer id) {
        Optional<Livros> livroOptional = livroRepository.findById(id);

        if (livroOptional.isPresent()) {
            return ResponseEntity.ok(livroOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
