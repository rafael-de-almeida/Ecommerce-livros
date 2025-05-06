package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    @Query("SELECT SUM(e.quantidade) FROM Estoque e WHERE e.livro.livId = :livroId")
    int totalEstoquePorLivro(@Param("livroId") Long livroId);
    @Query("SELECT e.livro.livId, CAST(SUM(e.quantidade) AS integer) FROM Estoque e GROUP BY e.livro.livId")
    List<Object[]> totalQuantidadePorLivro();

    List<Estoque> findByLivro_LivIdOrderByIdAsc(Long livroId);


}

