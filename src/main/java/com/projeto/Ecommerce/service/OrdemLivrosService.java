package com.projeto.Ecommerce.service;


import com.projeto.Ecommerce.dto.OrdemLivroResumoDTO;
import com.projeto.Ecommerce.repository.OrdemLivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdemLivrosService {

    @Autowired
    private OrdemLivroRepository ordemLivrosRepository;

    public List<OrdemLivroResumoDTO> findAllByOrdemId(Long ordId) {
        return ordemLivrosRepository.findByOrdemId(ordId);
    }
}
