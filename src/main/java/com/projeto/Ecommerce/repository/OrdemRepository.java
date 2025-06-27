package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Ordem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface OrdemRepository extends JpaRepository<Ordem, Long> {
    @Query("SELECT o FROM Ordem o " +
            "JOIN FETCH o.cliente c " +
            "LEFT JOIN FETCH o.livros ol " +
            "LEFT JOIN FETCH ol.livro l " +
            "WHERE (:nomeCliente IS NULL OR c.cliNome LIKE %:nomeCliente%) " +
            "AND (:tituloLivro IS NULL OR l.livTitulo LIKE %:tituloLivro%) " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (CAST(:dataInicio AS DATE) IS NULL OR o.data >= :dataInicio) " +
            "AND (CAST(:dataFim AS DATE) IS NULL OR o.data <= :dataFim) " +
            "AND (:valorTotal IS NULL OR o.precoTotal = :valorTotal) " +
            "AND (:numeroPedido IS NULL OR o.id = :numeroPedido)")
    List<Ordem> buscarOrdensComFiltros(
            @Param("nomeCliente") String nomeCliente,
            @Param("tituloLivro") String tituloLivro,
            @Param("status") String status,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("valorTotal") BigDecimal valorTotal,
            @Param("numeroPedido") Long numeroPedido // ← Novo parâmetro
    );
    @Query("SELECT o FROM Ordem o " +
            "LEFT JOIN FETCH o.livros ol " +
            "LEFT JOIN FETCH ol.livro l " +
            "WHERE o.id = :id")
    Ordem buscarOrdemComLivrosPorId(@Param("id") Long id);


    List<Ordem> findByStatus(String status);

}
