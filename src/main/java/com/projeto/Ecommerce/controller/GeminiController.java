package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/perguntar")
    public ResponseEntity<Map<String, String>> perguntar(@RequestBody Map<String, String> payload) {
        String pergunta = payload.get("pergunta");
        String usuarioId = payload.get("usuarioId");

        String resposta = geminiService.gerarResposta(pergunta, usuarioId);
        Map<String, String> respostaJson = new HashMap<>();
        respostaJson.put("resposta", resposta);
        return ResponseEntity.ok(respostaJson);
    }

}
