package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.CupomAplicadoDTO;
import com.projeto.Ecommerce.model.Cupom;
import com.projeto.Ecommerce.model.Ordem;
import com.projeto.Ecommerce.repository.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class CupomService {

    @Autowired
    private CupomRepository cupomRepository;

    /**
     * Valida se um cupom existe e pode ser usado pelo cliente
     */
    public Map<String, Object> validarCupom(String codigo, Integer clienteId) {
        Map<String, Object> resultado = new HashMap<>();

        Optional<Cupom> cupomOpt = cupomRepository.findCupomValidoParaCliente(codigo, clienteId);

        if (cupomOpt.isEmpty()) {
            resultado.put("valido", false);
            resultado.put("mensagem", "Cupom não encontrado ou não pertence a este cliente");
            return resultado;
        }

        Cupom cupom = cupomOpt.get();

        // Verifica se o cupom está ativo
        if (Objects.equals(cupom.getUsado(), cupom.getUsoMaximo())) {
            resultado.put("valido", false);
            resultado.put("mensagem", "Este cupom não está mais ativo");
            return resultado;
        }

        // Verifica a data de validade
        if (cupom.getValidade() != null &&
                cupom.getValidade().isBefore(LocalDate.now())) {
            resultado.put("valido", false);
            resultado.put("mensagem", "Este cupom expirou em " + cupom.getValidade());
            return resultado;
        }

        // Cupom válido
        resultado.put("valido", true);
        resultado.put("mensagem", "Cupom válido!");
        resultado.put("tipo", cupom.getTipo());
        resultado.put("valor", cupom.getValor());
        resultado.put("cupomId", cupom.getId());

        return resultado;
    }


    /**
     * Aplica o desconto do cupom a uma compra
     */
    public CupomAplicadoDTO aplicarCupom(String codigo, Integer clienteId, Double valorCompra) {
        Map<String, Object> resultadoValidacao = validarCupom(codigo, clienteId);

        // Verifica se a validação do cupom falhou
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

        // Calcula o desconto de acordo com o tipo do cupom
        if (tipo == Cupom.TipoDesconto.PROMOCIONAL) {
            valorDesconto = BigDecimal.valueOf(valorCompra).multiply(valor).divide(BigDecimal.valueOf(100));
        } else if (tipo == Cupom.TipoDesconto.TROCA) {
            valorDesconto = valor;
        }

        // Garante que o desconto não ultrapasse o valor da compra
        valorDesconto = valorDesconto.min(BigDecimal.valueOf(valorCompra));

        // Arredonda o valor do desconto para 2 casas decimais
        valorDesconto = valorDesconto.setScale(2, RoundingMode.HALF_UP);

        Double valorFinal = valorCompra - valorDesconto.doubleValue();

        // Criação do DTO de resposta
        CupomAplicadoDTO resposta = new CupomAplicadoDTO(
                true,
                "Cupom aplicado com sucesso!",
                valorCompra,
                valorDesconto.doubleValue(),
                valorFinal,
                cupomId
        );

        // Retornar apenas a resposta sem atualizar o cupom ainda
        return resposta;
    }

    // Método para marcar o cupom como "usado" na finalização da compra
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finalizarCompra(Long cupomId) {
        Optional<Cupom> cupomOpt = cupomRepository.findById(cupomId);
        if (cupomOpt.isPresent()) {
            Cupom cupom = cupomOpt.get();
            cupom.setUsado(cupom.getUsado() + 1);
            cupomRepository.save(cupom);
        } else {
            throw new RuntimeException("Cupom não encontrado.");
        }
    }

    public Optional<Cupom> buscarCupomPorOrigemTroca(Ordem ordem) {
        return cupomRepository.findByOrigemTroca(ordem);
    }

}

