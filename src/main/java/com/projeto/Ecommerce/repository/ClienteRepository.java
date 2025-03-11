package com.projeto.Ecommerce.repository;


import com.projeto.Ecommerce.model.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Clientes, Integer> {
    @Query("SELECT c FROM Clientes c WHERE " +
            "(:nome IS NULL OR c.cliNome LIKE %:nome%) AND " +
            "(:genero IS NULL OR c.cliGenero = :genero) AND " +
            "(:cpf IS NULL OR c.cliCpf = :cpf) AND " +
            "(:email IS NULL OR c.cliEmail LIKE %:email%) AND " +
            "(:telefone IS NULL OR c.cliTelefone = :telefone) AND " +
            "(:idade IS NULL OR c.cliIdade = :idade)")
    List<Clientes> findByFilters(
            @Param("nome") String nome,
            @Param("genero") String genero,
            @Param("cpf") String cpf,
            @Param("email") String email,
            @Param("telefone") String telefone,
            @Param("idade") Integer idade);
}
