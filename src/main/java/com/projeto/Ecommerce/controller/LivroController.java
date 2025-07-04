package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.model.Livros;
import com.projeto.Ecommerce.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/site")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping("/livros")
    public List<Livros> buscarLivros(
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String ano,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String editora,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer qtdpaginas,
            @RequestParam(required = false) String codbarras) {

        LocalDate anoConvertido = null;
        if (ano != null && !ano.trim().isEmpty()) {
            try {
                anoConvertido = LocalDate.parse(ano);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido para ano: " + ano);
            }
        }


        if (autor == null && categoria == null && ano == null && titulo == null &&
                editora == null && isbn == null && qtdpaginas == null && codbarras == null) {
            return livroRepository.findAll();
        }

        return livroRepository.buscarLivrosComFiltros(autor, categoria, anoConvertido, titulo, editora, isbn, qtdpaginas, codbarras);
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
