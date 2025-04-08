package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Ordem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemRepository extends JpaRepository<Ordem, Long> {
}
