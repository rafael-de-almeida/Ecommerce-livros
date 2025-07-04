package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.CupomAplicadoDTO;
import com.projeto.Ecommerce.dto.CupomListagemDTO;
import com.projeto.Ecommerce.model.Clientes;
import com.projeto.Ecommerce.model.Cupom;
import com.projeto.Ecommerce.model.Ordem;
import com.projeto.Ecommerce.repository.ClienteRepository;
import com.projeto.Ecommerce.repository.CupomRepository;
import com.projeto.Ecommerce.repository.OrdemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.projeto.Ecommerce.model.Cupom.TipoDesconto.TROCA;

@Service
public class CupomService {

    @Autowired
    private CupomRepository cupomRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private OrdemRepository ordemRepository;


    @Transactional(readOnly = true)
    public Map<String, Object> validarCupom(String codigo, Integer clienteId) {
        Map<String, Object> resultado = new HashMap<>();

        Optional<Cupom> cupomOpt = cupomRepository.findCupomValidoParaCliente(codigo, clienteId);

        if (cupomOpt.isEmpty()) {
            resultado.put("valido", false);
            resultado.put("mensagem", "Cupom não encontrado ou não pertence a este cliente");
            return resultado;
        }

        Cupom cupom = cupomOpt.get();

        if (Objects.equals(cupom.getUsado(), cupom.getUsoMaximo())) {
            resultado.put("valido", false);
            resultado.put("mensagem", "Este cupom não está mais ativo");
            return resultado;
        }

        if (cupom.getValidade() != null && cupom.getValidade().isBefore(LocalDate.now())) {
            resultado.put("valido", false);
            resultado.put("mensagem", "Este cupom expirou em " + cupom.getValidade());
            return resultado;
        }

        resultado.put("valido", true);
        resultado.put("mensagem", "Cupom válido!");
        resultado.put("tipo", cupom.getTipo());
        resultado.put("valor", cupom.getValor());
        resultado.put("cupomId", cupom.getId());

        return resultado;
    }


    public CupomAplicadoDTO aplicarCupom(String codigo, Integer clienteId, Double valorCompra) {
        Map<String, Object> resultadoValidacao = validarCupom(codigo, clienteId);

        if (!(Boolean) resultadoValidacao.get("valido")) {
            return new CupomAplicadoDTO(
                    false,
                    (String) resultadoValidacao.get("mensagem"),
                    valorCompra,
                    0.0,
                    valorCompra,
                    null
            );
        }

        Cupom.TipoDesconto tipo = (Cupom.TipoDesconto) resultadoValidacao.get("tipo");
        BigDecimal valor = (BigDecimal) resultadoValidacao.get("valor");
        Long cupomId = (Long) resultadoValidacao.get("cupomId");

        BigDecimal valorDesconto = BigDecimal.ZERO;

        if (tipo == Cupom.TipoDesconto.PROMOCIONAL) {
            valorDesconto = BigDecimal.valueOf(valorCompra).multiply(valor).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (tipo == TROCA) {
            valorDesconto = valor;
        }

        valorDesconto = valorDesconto.min(BigDecimal.valueOf(valorCompra));
        valorDesconto = valorDesconto.setScale(2, RoundingMode.HALF_UP);

        Double valorFinal = valorCompra - valorDesconto.doubleValue();

        return new CupomAplicadoDTO(
                true,
                "Cupom aplicado com sucesso!",
                valorCompra,
                valorDesconto.doubleValue(),
                valorFinal,
                cupomId
        );
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finalizarCompra(Long cupomId) {
        Cupom cupom = cupomRepository.findById(cupomId)
                .orElseThrow(() -> new RuntimeException("Cupom não encontrado para finalizar a compra: ID " + cupomId));

        cupom.setUsado(cupom.getUsado() + 1);
        cupomRepository.save(cupom);
    }

    @Transactional(readOnly = true)
    public Optional<Cupom> buscarCupomPorOrigemTroca(Ordem ordem) {
        return cupomRepository.findByOrigemTroca(ordem);
    }


    @Transactional
    public Cupom criarCupomTroca(Integer clienteId, BigDecimal valorCupom, Long idOrdemOrigem) {
        Clientes cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Cupom cupom = new Cupom();
        cupom.setCodigo("TROCA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        cupom.setTipo(TROCA);
        cupom.setValor(valorCupom);
        cupom.setTroca(true);
        cupom.setCliente(cliente);
        cupom.setUsado(0);
        cupom.setUsoMaximo(1);
        cupom.setValidade(LocalDate.now().plusYears(1)); // Validade de 1 ano

        if (idOrdemOrigem != null) {
            Ordem ordem = ordemRepository.findById(idOrdemOrigem)
                    .orElseThrow(() -> new RuntimeException("Ordem não encontrada"));
            cupom.setOrigemTroca(ordem);
        }

        return cupomRepository.save(cupom);
    }


    @Transactional
    public Cupom gerarCupomDeTroco(Integer clienteId, BigDecimal valorTroco) {
        if (valorTroco == null || valorTroco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do troco deve ser positivo.");
        }

        Clientes cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + clienteId));

        Cupom novoCupom = new Cupom();
        novoCupom.setCodigo("TRC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        novoCupom.setTipo(TROCA);
        novoCupom.setTroca(true);
        novoCupom.setValor(valorTroco);
        novoCupom.setCliente(cliente);
        novoCupom.setUsado(0);
        novoCupom.setUsoMaximo(1);
        novoCupom.setValidade(LocalDate.now().plusYears(1)); // Validade de 1 ano

        return cupomRepository.save(novoCupom);
    }

    @Transactional(readOnly = true)
    public List<CupomListagemDTO> listarCuponsDeTrocaPorCliente(Integer clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + clienteId);
        }

        List<Cupom> cuponsDeTroca = cupomRepository.findCuponsDeTrocaValidosPorCliente(clienteId);


        return cuponsDeTroca.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    private CupomListagemDTO converterParaDTO(Cupom cupom) {
        String descricao = gerarDescricaoDoCupom(cupom);
        return new CupomListagemDTO(
                cupom.getCodigo(),
                cupom.getValor(),
                cupom.getTipo(),
                cupom.getValidade(),
                descricao
        );
    }


    private String gerarDescricaoDoCupom(Cupom cupom) {
        if (cupom.getTipo() == Cupom.TipoDesconto.PROMOCIONAL) {

            return cupom.getValor().stripTrailingZeros().toPlainString() + "% de desconto.";
        } else if (cupom.getTipo() == Cupom.TipoDesconto.TROCA) {

            return "Crédito de R$ " + String.format("%.2f", cupom.getValor().doubleValue()).replace('.', ',') + " para usar em sua compra.";
        }
        return "Cupom de desconto.";
    }
}