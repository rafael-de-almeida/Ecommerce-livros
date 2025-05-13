package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.dto.CupomAplicadoDTO;
import com.projeto.Ecommerce.dto.CupomRequestDTO;
import com.projeto.Ecommerce.dto.FinalizarCompraRequest;
import com.projeto.Ecommerce.model.Cupom;
import com.projeto.Ecommerce.model.Ordem;
import com.projeto.Ecommerce.service.CupomService;
import com.projeto.Ecommerce.service.OrdemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;

@RestController
@RequestMapping("/api/cupons")
@CrossOrigin(origins = "*")
public class CupomController {

    @Autowired
    private CupomService cupomService;
    @Autowired
    private OrdemService ordemService;
    @PostMapping("/validar")
    public ResponseEntity<?> validarCupom(@RequestBody CupomRequestDTO cupomRequest) {
        String codigoCupom = cupomRequest.getCodigo();
        Integer clienteId = cupomRequest.getClienteId();

        // Validação do código do cupom
        if (codigoCupom == null || codigoCupom.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valido", false,
                    "mensagem", "Código de cupom não fornecido"
            ));
        }

        try {
            // Chama o serviço para validar o cupom
            Map<String, Object> resultado = cupomService.validarCupom(codigoCupom, clienteId);

            // Retorna o resultado da validação
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            // Em caso de erro, retorna uma resposta com o erro
            return ResponseEntity.badRequest().body(Map.of(
                    "valido", false,
                    "mensagem", "Erro ao validar cupom: " + e.getMessage()
            ));
        }
    }


    @PostMapping("/aplicar")
    public ResponseEntity<CupomAplicadoDTO> aplicarCupom(@RequestBody Map<String, Object> body) {
        try {
            String codigoCupom = (String) body.get("codigo");
            Integer clienteId = Integer.parseInt(body.get("clienteId").toString());
            Double valorCompra = Double.parseDouble(body.get("valorCompra").toString());

            // Chama o serviço para aplicar o cupom e retorna o DTO
            CupomAplicadoDTO resposta = cupomService.aplicarCupom(codigoCupom, clienteId, valorCompra);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new CupomAplicadoDTO(
                    false,
                    "Erro ao aplicar cupom: " + e.getMessage(),
                    0.0,
                    0.0,
                    0.0,
                    null
            ));
        }
    }
    @PostMapping("/finalizar-compra")
    public ResponseEntity<?> finalizarCompra(@RequestBody FinalizarCompraRequest request) {
        try {
            // Verifica se há cupons a serem utilizados
            List<Long> cuponsIds = request.getCuponsIds();
            if (cuponsIds != null && !cuponsIds.isEmpty()) {
                for (Long cupomId : cuponsIds) {
                    System.out.println("Usando cupom ID: " + cupomId);
                    cupomService.finalizarCompra(cupomId);
                }
            }

            // Outras lógicas de finalização de compra (ex: atualizar ordem, pagamento, etc.)

            return ResponseEntity.ok(Map.of("sucesso", true, "mensagem", "Compra finalizada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", "Erro ao finalizar a compra: " + e.getMessage()
            ));
        }
    }


    @GetMapping("/buscar-por-origem-troca/{idOrdem}")
    public ResponseEntity<Cupom> buscarCupomPorOrigemTroca(@PathVariable Long idOrdem) {
        // Buscar ordem pelo ID
        Optional<Ordem> ordem = ordemService.buscarOrdemPorId(idOrdem); // Aqui você usa o service de Ordem para buscar a ordem
        if (ordem.isPresent()) {
            Optional<Cupom> cupom = cupomService.buscarCupomPorOrigemTroca(ordem.get());
            return cupom.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }


}