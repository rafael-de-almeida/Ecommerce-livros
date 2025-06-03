package com.projeto.Ecommerce.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.projeto.Ecommerce.model.Livros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final Client client;

    private final Map<String, List<String>> historicoPorUsuario = new HashMap<>();

    // Novo mapa para armazenar o último livro mencionado por usuário
    private final Map<String, String> ultimoLivroPorUsuario = new HashMap<>();

    @Autowired
    private LivroService livroService;

    public GeminiService(@Value("${google.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String gerarResposta(String prompt, String usuarioId) {
        historicoPorUsuario.putIfAbsent(usuarioId, new ArrayList<>());
        List<String> historico = historicoPorUsuario.get(usuarioId);

        // Detecta se o prompt é vago (exemplos simples)
        boolean promptVago = prompt.matches("(?i).*\\b(qual é o seu gênero\\??|qual o gênero\\??|me fale mais dele|fale mais dele|qual é o gênero\\??)\\b.*");

        String promptParaIA = prompt;

        if (promptVago) {
            String ultimoLivro = ultimoLivroPorUsuario.get(usuarioId);
            if (ultimoLivro != null) {
                // Adiciona contexto extra sem substituir o prompt original
                promptParaIA = prompt + "\n(Considere o livro: " + ultimoLivro + ")";
            }
        }

        // Buscar livros relacionados com promptParaIA
        List<String> livrosRelacionados = livroService.encontrarLivrosRelacionados(promptParaIA);
        if (livrosRelacionados.isEmpty()) {
            String ultimoLivro = ultimoLivroPorUsuario.get(usuarioId);
            if (ultimoLivro != null) {
                livrosRelacionados = List.of(ultimoLivro);
            } else {
                livrosRelacionados = livroService.obterTitulos();
            }
        } else {
            ultimoLivroPorUsuario.put(usuarioId, livrosRelacionados.get(0));
        }

        // Buscar autores relacionados
        List<String> autoresRelacionados = livroService.encontrarAutoresRelacionados(promptParaIA);
        if (autoresRelacionados.isEmpty()) {
            autoresRelacionados = livroService.obterAutores();
        }

        // Monta a entrada do usuário com o promptParaIA
        String entradaUsuario = "Usuário: " + promptParaIA;
        if (!livrosRelacionados.isEmpty()) {
            entradaUsuario += " (livros: " + String.join(", ", livrosRelacionados) + ")";
        }
        historico.add(entradaUsuario);

        String contextoCompleto = String.join("\n", historico);

        // Recupera todos os livros para sinopses
        List<Livros> todosLivros = livroService.obterTodosLivros();

        // Monta o contexto para enviar para a IA
        StringBuilder contexto = new StringBuilder();
        contexto.append("Você é um assistente virtual de uma livraria. ")
                .append("Sua função é responder APENAS com base nos livros e autores listados a seguir. ")
                .append("NÃO invente livros, autores ou informações que não estejam explícitas. ")
                .append("Se o conteúdo solicitado não estiver na lista, diga que não foi encontrado.\n\n");

        if (!livrosRelacionados.isEmpty()) {
            contexto.append("Livros encontrados:\n");
            livrosRelacionados.forEach(t -> contexto.append("- ").append(t).append("\n"));
        }

        if (!autoresRelacionados.isEmpty()) {
            contexto.append("\nAutores encontrados:\n");
            autoresRelacionados.forEach(a -> contexto.append("- ").append(a).append("\n"));
        }

        contexto.append("\nSinopses disponíveis:\n");
        for (Livros livro : todosLivros) {
            if (livrosRelacionados.contains(livro.getLivTitulo())) {
                contexto.append("- ").append(livro.getLivTitulo()).append(": ")
                        .append(livro.getLivSinopse() != null ? livro.getLivSinopse() : "Sinopse não disponível.")
                        .append("\n");
            }
        }

        // Debug
        System.out.println(">>> Prompt recebido: " + prompt);
        System.out.println(">>> Prompt para IA: " + promptParaIA);
        System.out.println(">>> Livros relacionados: " + livrosRelacionados);
        System.out.println(">>> Autores relacionados: " + autoresRelacionados);
        System.out.println(">>> Texto final enviado à IA:\n" + contexto);

        contexto.append("\n\nHistórico da conversa:\n").append(contextoCompleto);

        GenerateContentResponse response = client.models.generateContent(
                "gemini-2.0-flash",
                contexto.toString(),
                null
        );

        String resposta = response.text();
        historico.add("IA: " + resposta);

        return resposta;
    }
}
