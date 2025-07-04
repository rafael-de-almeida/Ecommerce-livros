package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.LivroQuantidadeDTO;
import com.projeto.Ecommerce.dto.PedidoResumoDTO;
import com.projeto.Ecommerce.model.Categorias;
import com.projeto.Ecommerce.repository.OrdemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GraficoService {


    private final OrdemRepository ordemRepository;

    public GraficoService(OrdemRepository ordemRepository) {
        this.ordemRepository = ordemRepository;
    }

    public List<PedidoResumoDTO> listarPedidosEntregues() {
        return ordemRepository.findByStatus("ENTREGUE").stream().map(ordem -> {
            List<LivroQuantidadeDTO> livros = ordem.getLivros().stream().map(ol -> {
                List<String> categorias = ol.getLivro().getCategorias().stream()
                        .map(Categorias::getNome)
                        .collect(Collectors.toList());

                return new LivroQuantidadeDTO(
                        ol.getLivro().getLivTitulo(),
                        ol.getQuantidade(),
                        categorias
                );
            }).collect(Collectors.toList());

            return new PedidoResumoDTO(
                    ordem.getId(),
                    ordem.getData(),
                    ordem.getStatus(),
                    livros
            );
        }).collect(Collectors.toList());
    }


}