package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.dto.LivroResumoDTO;
import com.projeto.Ecommerce.model.OrdemLivro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemLivroRepository extends JpaRepository<OrdemLivro, Long> {

    @Query("SELECT new com.projeto.Ecommerce.dto.LivroResumoDTO(l.livTitulo, ol.quantidade, ol.preco) " +
            "FROM OrdemLivro ol JOIN ol.livro l WHERE ol.ordem.id = :ordemId")

    List<LivroResumoDTO> buscarLivrosDaOrdem(@Param("ordemId") Long ordemId);

}

