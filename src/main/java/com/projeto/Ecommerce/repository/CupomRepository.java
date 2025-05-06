package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Cupom;
import com.projeto.Ecommerce.model.Ordem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {
    Optional<Cupom> findByCodigoAndCliente_CliId(String codigo, Integer cliente_cliId);

    boolean existsByOrigemTroca_Id(Long id);

    Optional<Cupom> findByOrigemTroca(Ordem ordem);
}
