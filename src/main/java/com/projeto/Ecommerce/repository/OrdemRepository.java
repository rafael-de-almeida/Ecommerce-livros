package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Ordem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrdemRepository extends JpaRepository<Ordem, Long> {
    @Query("SELECT o FROM Ordem o " +
            "JOIN FETCH o.cliente c " +
            "LEFT JOIN FETCH o.livros ol " +
            "LEFT JOIN FETCH ol.livro l " +
            "WHERE (:nomeCliente IS NULL OR c.cliNome LIKE %:nomeCliente%) " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:dataInicio IS NULL OR o.data >= :dataInicio) " +
            "AND (:dataFim IS NULL OR o.data <= :dataFim)")
    List<Ordem> buscarOrdensComFiltros(
            @Param("nomeCliente") String nomeCliente,
            @Param("status") String status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );



}
