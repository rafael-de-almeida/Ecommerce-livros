package com.projeto.Ecommerce.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.projeto.Ecommerce.dto.LivroResumoDTO;
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

    @Autowired
    private OrdemService ordemService;



    public GeminiService(@Value("${google.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    public String gerarResposta(String prompt, String usuarioId) {
        historicoPorUsuario.putIfAbsent(usuarioId, new ArrayList<>());
        List<String> historico = historicoPorUsuario.get(usuarioId);

        // Monta o histórico da conversa
        String entradaUsuario = "Usuário: " + prompt;
        historico.add(entradaUsuario);
        String contextoCompleto = String.join("\n", historico);

        // Busca todos os livros e autores
        List<Livros> todosLivros = livroService.obterTodosLivros();
        List<String> livrosRelacionados = livroService.obterTitulos();
        List<String> autoresRelacionados = livroService.obterAutores();

        // Monta o contexto completo para enviar à IA
        StringBuilder contexto = new StringBuilder();
        contexto.append("Você é um assistente virtual de uma livraria, você só pode recomendar livros, e não pode realizar nenhuma compra. ")
                .append("Sua função é responder APENAS com base nos livros e autores listados a seguir. ")
                .append("NÃO invente livros, autores ou informações que não estejam explícitas. ")
                .append("Se o conteúdo solicitado não estiver na lista, diga: 'Infelizmente, no momento, não possuímos esse livro em nosso catálogo. Estamos sempre atualizando nosso acervo! Quer uma outra recomendação de livro?'\n\n");


        if (!livrosRelacionados.isEmpty()) {
            contexto.append("Livros disponíveis:\n");
            livrosRelacionados.forEach(t -> contexto.append("- ").append(t).append("\n"));
        }

        if (!autoresRelacionados.isEmpty()) {
            contexto.append("\nAutores disponíveis:\n");
            autoresRelacionados.forEach(a -> contexto.append("- ").append(a).append("\n"));
        }

        contexto.append("\nSinopses dos livros:\n");
        for (Livros livro : todosLivros) {
            contexto.append("- ").append(livro.getLivTitulo()).append(": ")
                    .append(livro.getLivSinopse() != null ? livro.getLivSinopse() : "Sinopse não disponível.")
                    .append("\n");
        }

        contexto.append("\nGêneros dos livros:\n");
        for (Livros livro : todosLivros) {
            contexto.append("- ").append(livro.getLivTitulo()).append(": ");
            if (livro.getCategorias() != null && !livro.getCategorias().isEmpty()) {
                List<String> nomes = livro.getCategorias().stream()
                        .map(c -> c.getNome())
                        .toList();
                contexto.append(String.join(", ", nomes));
            } else {
                contexto.append("Gênero não disponível");
            }
            contexto.append("\n");
        }

        contexto.append("\n\nHistórico da conversa:\n").append(contextoCompleto);

        int numero = 1;
        try {
            Long clienteId = Long.parseLong(usuarioId);
            List<LivroResumoDTO> historicoCompras = ordemService.listarLivrosDaOrdem(clienteId);
            if (!historicoCompras.isEmpty()) {
                contexto.append("\nHistórico de compras do cliente:\n");
                for (LivroResumoDTO livro : historicoCompras) {
                    contexto.append("pedido numero:").append(numero).append(" ").append(livro.getTitulo()).append(" ");
                    numero++;
                }
            }
        } catch (NumberFormatException e) {
            contexto.append("\n(Não foi possível interpretar o ID do cliente para obter histórico)\n");
        }

        contexto.append("\n\nHistórico da conversa:\n").append(contextoCompleto);

        // Debug
        System.out.println(">>> Prompt recebido: " + prompt);
        System.out.println(">>> Texto final enviado à IA:\n" + contexto);

        // Chamada à API do Gemini
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
