package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.dto.*;
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

        
        if (codigoCupom == null || codigoCupom.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valido", false,
                    "mensagem", "Código de cupom não fornecido"
            ));
        }

        try {

            Map<String, Object> resultado = cupomService.validarCupom(codigoCupom, clienteId);


            return ResponseEntity.ok(resultado);
        } catch (Exception e) {

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
    @PostMapping("/gerar-troco")
    public ResponseEntity<?> gerarTroco(@RequestBody CupomTrocoDTO request) {
        try {

            Cupom cupomGerado = cupomService.gerarCupomDeTroco(
                    request.getClienteId(),
                    request.getValor()
            );

            return ResponseEntity.ok(cupomGerado);
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(Map.of("sucesso", false, "mensagem", e.getMessage()));
        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(Map.of("sucesso", false, "mensagem", e.getMessage()));
        }
    }

    @PostMapping("/finalizar-compra")
    public ResponseEntity<?> finalizarCompra(@RequestBody FinalizarCompraRequest request) {
        try {

            List<Long> cuponsIds = request.getCuponsIds();
            if (cuponsIds != null && !cuponsIds.isEmpty()) {
                for (Long cupomId : cuponsIds) {
                    System.out.println("Usando cupom ID: " + cupomId);
                    cupomService.finalizarCompra(cupomId);
                }
            }


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
    @PostMapping("/criar-troca")
    public ResponseEntity<?> criarCupomTroca(@RequestBody CupomTrocaRequestDTO requestDTO) {
        try {

            Cupom cupomCriado = cupomService.criarCupomTroca(
                    requestDTO.getClienteId(),
                    requestDTO.getValorEmCentavos(),
                    requestDTO.getIdOrdemOrigem()
            );

            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", "Cupom de troca gerado com sucesso.",
                    "cupom", cupomCriado
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", "Erro ao gerar cupom de troca: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/cliente/{clienteId}/troca")
    public ResponseEntity<?> listarCuponsDeTrocaDoCliente(@PathVariable Integer clienteId) {
        try {
            List<CupomListagemDTO> cuponsDTO = cupomService.listarCuponsDeTrocaPorCliente(clienteId);
            return ResponseEntity.ok(cuponsDTO);
        } catch (RuntimeException e) {
            System.err.println("Erro ao Buscar Cupons: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno ao buscar cupons de troca.");
        }
    }


}