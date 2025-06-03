package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.model.Livros;
import com.projeto.Ecommerce.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    public List<String> obterTitulos() {
        return livroRepository.findTitulos();
    }

    public List<String> obterAutores() {
        return livroRepository.findAutores();
    }

    public List<Livros> obterTodosLivros() {
        return livroRepository.findAllWithCategorias();
    }

    public List<String> encontrarLivrosRelacionados(String prompt) {
        return filtrarRelacionados(prompt, this::obterTitulos);
    }

    public List<String> encontrarAutoresRelacionados(String prompt) {
        return filtrarRelacionados(prompt, this::obterAutores);
    }

    private List<String> filtrarRelacionados(String prompt, Supplier<List<String>> fornecedor) {
        if (prompt == null || prompt.isBlank()) return fornecedor.get();

        String texto = prompt.toLowerCase();
        String[] palavrasPrompt = texto.split("\\s+");
        List<String> candidatos = fornecedor.get();
        List<String> resultados = new ArrayList<>();

        for (String item : candidatos) {
            if (item == null) continue;
            String itemLower = item.toLowerCase();

            if (texto.contains(itemLower) || contemPalavra(itemLower, palavrasPrompt)) {
                resultados.add(item);
            }
        }

        return resultados.isEmpty() ? fornecedor.get() : resultados;
    }

    private boolean contemPalavra(String texto, String[] palavras) {
        for (String palavra : palavras) {
            if (texto.contains(palavra)) return true;
        }
        return false;
    }

}