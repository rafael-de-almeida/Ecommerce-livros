package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Cupom;
import com.projeto.Ecommerce.model.Ordem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {
    Optional<Cupom> findByCodigoAndCliente_CliId(String codigo, Integer cliente_cliId);

    boolean existsByOrigemTroca_Id(Long id);

    Optional<Cupom> findByOrigemTroca(Ordem ordem);

    @Query("SELECT c FROM Cupom c WHERE c.codigo = :codigo AND (c.cliente IS NULL OR c.cliente.cliId = :clienteId)")
    Optional<Cupom> findCupomValidoParaCliente(String codigo, Integer clienteId);


    @Query("SELECT c FROM Cupom c " +
            "WHERE c.cliente.cliId = :clienteId " +
            "AND c.tipo = com.projeto.Ecommerce.model.Cupom.TipoDesconto.TROCA " +
            "AND c.usado < c.usoMaximo " +
            "AND (c.validade IS NULL OR c.validade >= CURRENT_DATE)")
    List<Cupom> findCuponsDeTrocaValidosPorCliente(@Param("clienteId") Integer clienteId);

}
