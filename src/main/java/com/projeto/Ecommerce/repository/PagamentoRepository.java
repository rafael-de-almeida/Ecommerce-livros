package com.projeto.Ecommerce.repository;

import com.projeto.Ecommerce.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    List<Pagamento> findByOrdem_Id(Long id);
}
