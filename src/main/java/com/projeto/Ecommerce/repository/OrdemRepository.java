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
    @Query("SELECT o FROM Ordem o JOIN o.livros ol JOIN ol.livro l JOIN l.categorias c " +
            "WHERE (:nomeCliente IS NULL OR o.cliente.cliNome LIKE %:nomeCliente%) " +
            "AND (:tituloLivro IS NULL OR l.livTitulo LIKE %:tituloLivro%) " +
            "AND (:categoria IS NULL OR c.nome LIKE %:categoria%) " +  // <-- Filtro novo
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:dataInicio IS NULL OR o.data >= :dataInicio) " +
            "AND (:dataFim IS NULL OR o.data <= :dataFim) " +
            "AND (:valorTotal IS NULL OR o.precoTotal = :valorTotal) " +
            "AND (:numeroPedido IS NULL OR o.id = :numeroPedido)")
    List<Ordem> buscarOrdensComFiltros(@Param("nomeCliente") String nomeCliente,
                                       @Param("tituloLivro") String tituloLivro,
                                       @Param("categoria") String categoria,
                                       @Param("status") String status,
                                       @Param("dataInicio") LocalDate dataInicio,
                                       @Param("dataFim") LocalDate dataFim,
                                       @Param("valorTotal") BigDecimal valorTotal,
                                       @Param("numeroPedido") Long numeroPedido);

    @Query("SELECT o FROM Ordem o " +
            "LEFT JOIN FETCH o.livros ol " +
            "LEFT JOIN FETCH ol.livro l " +
            "WHERE o.id = :id")
    Ordem buscarOrdemComLivrosPorId(@Param("id") Long id);


    List<Ordem> findByStatus(String status);

}
