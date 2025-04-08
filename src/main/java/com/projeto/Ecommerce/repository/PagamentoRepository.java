package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
}
