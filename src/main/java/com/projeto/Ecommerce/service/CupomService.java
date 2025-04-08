package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.model.Clientes;
import com.projeto.Ecommerce.model.Cupom;
import com.projeto.Ecommerce.repository.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CupomService {

    @Autowired
    private CupomRepository cupomRepository;

    public Cupom gerarCupomTroca(BigDecimal valor, Clientes cliente) {
        Cupom cupom = new Cupom();
        cupom.setCodigo("TROCA-" + UUID.randomUUID().toString().substring(0, 8));
        cupom.setValor(valor);
        cupom.setTipo(Cupom.TipoDesconto.FIXO);
        cupom.setTroca(true);
        cupom.setValidade(LocalDate.now().plusMonths(6));
        cupom.setCliente(cliente);
        return cupomRepository.save(cupom);
    }
}

