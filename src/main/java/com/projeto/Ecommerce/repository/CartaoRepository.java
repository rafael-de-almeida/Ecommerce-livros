package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Cartoes;
import com.projeto.Ecommerce.model.Enderecos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaoRepository extends JpaRepository<Cartoes, Integer> {
    List<Cartoes> findByCliente_CliId(Integer cliId);
}
