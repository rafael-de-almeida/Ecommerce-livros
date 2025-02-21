package com.projeto.Ecommerce.repository;


import com.projeto.Ecommerce.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.service.annotation.PostExchange;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
