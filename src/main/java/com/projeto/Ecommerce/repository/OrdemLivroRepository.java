package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.dto.LivroResumoDTO;
import com.projeto.Ecommerce.dto.OrdemLivroResumoDTO;
import com.projeto.Ecommerce.model.OrdemLivro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemLivroRepository extends JpaRepository<OrdemLivro, Long> {

    @Query("SELECT new com.projeto.Ecommerce.dto.LivroResumoDTO(c.cliId, o.id, o.data, l.livId ,l.livTitulo, ol.quantidade, ol.preco, o.status, o.precoTotal) " +
            "FROM OrdemLivro ol " +
            "JOIN ol.livro l " +
            "JOIN ol.ordem o " +
            "JOIN o.cliente c " +
            "WHERE c.cliId = :clienteId")
    List<LivroResumoDTO> findPedidosByClienteId(@Param("clienteId") Long clienteId);


    @Query("SELECT new com.projeto.Ecommerce.dto.OrdemLivroResumoDTO(ol.id, o.id, l.livId, l.livTitulo, ol.quantidade, ol.preco) " +
            "FROM OrdemLivro ol " +
            "JOIN ol.livro l " +
            "JOIN ol.ordem o " +
            "WHERE o.id = :ordemId")
    List<OrdemLivroResumoDTO> findByOrdemId(@Param("ordemId") Long ordemId);


}

