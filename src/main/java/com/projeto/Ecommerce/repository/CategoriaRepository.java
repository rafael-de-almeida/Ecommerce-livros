package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categorias, Integer> {
    @Query("SELECT c.nome FROM Categorias c JOIN c.livros l WHERE l.livId = :livroId")
    List<String> findNomesByLivroId(@Param("livroId") Long livroId);
}
