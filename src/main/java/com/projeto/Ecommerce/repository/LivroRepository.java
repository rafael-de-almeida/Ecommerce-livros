package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Livros;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livros, Integer> {

}
