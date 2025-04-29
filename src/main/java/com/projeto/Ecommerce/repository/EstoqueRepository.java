package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    // Aqui você pode adicionar métodos personalizados se necessário
}

