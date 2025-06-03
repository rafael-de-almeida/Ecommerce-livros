package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.model.Livros;
import com.projeto.Ecommerce.repository.LivroRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    private final LevenshteinDistance distance = new LevenshteinDistance();

    public List<String> obterTitulos() {
        return livroRepository.findTitulos();
    }

    public List<String> encontrarLivrosRelacionados(String prompt) {
        String texto = prompt.toLowerCase();
        List<String> titulos = livroRepository.findTitulos();
        List<String> resultados = new ArrayList<>();

        // Quebrar o prompt em palavras
        String[] palavrasPrompt = texto.split("\\s+");

        for (String titulo : titulos) {
            if (titulo == null) continue;

            String tituloLower = titulo.toLowerCase();

            boolean encontrado = false;

            // Se o título está contido no prompt, aceita direto
            if (texto.contains(tituloLower)) {
                resultados.add(titulo);
                continue;
            }

            // Se alguma palavra do prompt está contida no título, aceita direto
            for (String palavra : palavrasPrompt) {
                if (tituloLower.contains(palavra)) {
                    resultados.add(titulo);
                    encontrado = true;
                    break;
                }
            }
            if (encontrado) continue;

            // Caso contrário, calcula similaridade com todo o prompt e também com palavras isoladas
            int dist = distance.apply(tituloLower, texto);
            double similarity = 1.0 - ((double) dist / Math.max(tituloLower.length(), texto.length()));
            if (similarity >= 0.7) {
                resultados.add(titulo);
                continue;
            }

            // Testa similaridade com palavras individuais do prompt
            for (String palavra : palavrasPrompt) {
                dist = distance.apply(tituloLower, palavra);
                similarity = 1.0 - ((double) dist / Math.max(tituloLower.length(), palavra.length()));
                if (similarity >= 0.7) {
                    resultados.add(titulo);
                    break;
                }
            }
        }

        return resultados;
    }

    public List<String> encontrarAutoresRelacionados(String prompt) {
        String texto = prompt.toLowerCase();
        List<String> autores = livroRepository.findAutores();
        List<String> resultados = new ArrayList<>();

        // Quebrar o prompt em palavras
        String[] palavrasPrompt = texto.split("\\s+");

        for (String autor : autores) {
            if (autor == null) continue;

            String autorLower = autor.toLowerCase();

            boolean encontrado = false;

            // Se o nome do autor está contido no prompt, aceita direto
            if (texto.contains(autorLower)) {
                resultados.add(autor);
                continue;
            }

            // Se alguma palavra do prompt está contida no nome do autor, aceita direto
            for (String palavra : palavrasPrompt) {
                if (autorLower.contains(palavra)) {
                    resultados.add(autor);
                    encontrado = true;
                    break;
                }
            }
            if (encontrado) continue;

            // Caso contrário, calcula similaridade com todo o prompt e também com palavras isoladas
            int dist = distance.apply(autorLower, texto);
            double similarity = 1.0 - ((double) dist / Math.max(autorLower.length(), texto.length()));
            if (similarity >= 0.7) {
                resultados.add(autor);
                continue;
            }

            // Testa similaridade com palavras individuais do prompt
            for (String palavra : palavrasPrompt) {
                dist = distance.apply(autorLower, palavra);
                similarity = 1.0 - ((double) dist / Math.max(autorLower.length(), palavra.length()));
                if (similarity >= 0.7) {
                    resultados.add(autor);
                    break;
                }
            }
        }

        return resultados;
    }

    public List<String> obterAutores() {
        return livroRepository.findAutores();
    }
    public List<Livros> obterTodosLivros() {
        return livroRepository.findAllLivros();
    }
}
