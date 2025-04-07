package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.LivroResumoDTO;
import com.projeto.Ecommerce.repository.OrdemLivroRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdemService {

    private final OrdemLivroRepository ordemLivroRepository;

    public OrdemService(OrdemLivroRepository ordemLivroRepository) {
        this.ordemLivroRepository = ordemLivroRepository;
    }

    public List<LivroResumoDTO> listarLivrosDaOrdem(Long ordemId) {
        return ordemLivroRepository.buscarLivrosDaOrdem(ordemId);
    }
}
