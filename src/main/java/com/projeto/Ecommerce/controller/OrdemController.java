package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.dto.LivroResumoDTO;
import com.projeto.Ecommerce.dto.OrdemRequestDTO;
import com.projeto.Ecommerce.dto.OrdemResumoDTO;
import com.projeto.Ecommerce.dto.TrocaRequestDTO;
import com.projeto.Ecommerce.model.Ordem;
import com.projeto.Ecommerce.repository.OrdemRepository;
import com.projeto.Ecommerce.service.OrdemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/site")
public class OrdemController {

    @Autowired
    private OrdemService ordemService;

    @Autowired
    private OrdemRepository ordemRepository;


    @GetMapping("/clientes/pedido/get/{id}")
    public ResponseEntity<List<LivroResumoDTO>> getLivrosDaOrdem(@PathVariable Long id) {
        List<LivroResumoDTO> livros = ordemService.listarLivrosDaOrdem(id);
        return ResponseEntity.ok(livros);
    }

    @PostMapping("/clientes/pedido/post")
    public ResponseEntity<String> criarOrdem(@RequestBody OrdemRequestDTO ordemRequestDTO) {
        try {
            ordemService.criarOrdem(ordemRequestDTO);
            return ResponseEntity.ok("Ordem criada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar ordem: " + e.getMessage());
        }
    }


@GetMapping("/ordens/resumo")
public List<OrdemResumoDTO> buscarOrdens(
        @RequestParam(required = false) String nomeCliente,
        @RequestParam(required = false) String tituloLivro,
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) BigDecimal valorTotal,
        @RequestParam(required = false) Long numeroPedido
) {
    return ordemService.buscarOrdens(nomeCliente, tituloLivro, categoria, status, dataInicio, dataFim, valorTotal, numeroPedido);
}



    @PutMapping("ordens/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String novoStatus = body.get("status");

        if (novoStatus == null || novoStatus.isEmpty()) {
            return ResponseEntity.badRequest().body("Status não fornecido");
        }

        try {

            Ordem ordem = ordemRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem não encontrada"));


            ordemService.atualizarStatusTroca(id, novoStatus);

            return ResponseEntity.ok("Status atualizado com sucesso");
        } catch (ResponseStatusException e) {

            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar status: " + e.getMessage());
        }
    }
    @PatchMapping("/clientes/pedido/troca")
    public ResponseEntity<String> solicitarTroca(@RequestBody TrocaRequestDTO trocaRequestDTO) {
        try {
            ordemService.solicitarTroca(trocaRequestDTO);
            return ResponseEntity.ok("Ordem de troca criada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }
    }

}

