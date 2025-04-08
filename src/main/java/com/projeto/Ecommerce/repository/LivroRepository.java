package com.projeto.Ecommerce.repository;


import com.projeto.Ecommerce.model.Livros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livros, Integer> {

        @Query("SELECT l FROM Livros l WHERE " +
                "(:autor IS NULL OR l.livAutor LIKE %:autor%) " +
                "AND (:categoria IS NULL OR l.livCategoria = :categoria) " +
                "AND (:ano IS NULL OR l.livAno = :ano)" +
                "AND (:titulo IS NULL OR l.livTitulo LIKE %:titulo%) " +
                "AND (:editora IS NULL OR l.livEditora = :editora) " +
                "AND (:isbn IS NULL OR l.livIsbn = :isbn) " +
                "AND (:qtdpaginas IS NULL OR l.livQtdPaginas = :qtdpaginas) " +
                "AND (:codbarras IS NULL OR l.liv_cod_barras = :codbarras)")
        List<Livros> findLivrosBy(@Param("autor") String autor,
                                  @Param("categoria") String categoria,
                                  @Param("ano") Integer ano,
                                  @Param("titulo") String titulo,
                                  @Param("editora") String editora,
                                  @Param("isbn") String isbn,
                                  @Param("qtdpaginas") Integer qtdpaginas,
                                  @Param("codbarras") String codbarras);

}
