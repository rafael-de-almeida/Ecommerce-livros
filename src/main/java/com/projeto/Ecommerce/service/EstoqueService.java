package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.EstoqueCreateDTO;
import com.projeto.Ecommerce.dto.EstoqueDTO;
import com.projeto.Ecommerce.model.Estoque;
import com.projeto.Ecommerce.model.Livros;
import com.projeto.Ecommerce.repository.EstoqueRepository;
import com.projeto.Ecommerce.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository repository;

    @Autowired
    private LivroRepository livroRepository;

    public List<EstoqueDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public EstoqueDTO buscarPorId(Long id) {
        Estoque estoque = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        return toDTO(estoque);
    }

    public EstoqueDTO adicionar(EstoqueCreateDTO dto) {
        Livros livro = livroRepository.findById(dto.getIdLivro())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Estoque estoque = new Estoque();
        estoque.setLivro(livro);
        estoque.setDataEntrada(dto.getDataEntrada());
        estoque.setFornecedor(dto.getFornecedor());
        estoque.setQuantidade(dto.getQuantidade());
        estoque.setValorDeCusto(dto.getValorDeCusto());

        return toDTO(repository.save(estoque));
    }

    public EstoqueDTO atualizar(Long id, EstoqueCreateDTO dto) {
        Estoque estoque = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        Livros livro = livroRepository.findById(dto.getIdLivro())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        estoque.setLivro(livro);
        estoque.setDataEntrada(dto.getDataEntrada());
        estoque.setFornecedor(dto.getFornecedor());
        estoque.setQuantidade(dto.getQuantidade());
        estoque.setValorDeCusto(dto.getValorDeCusto());

        return toDTO(repository.save(estoque));
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Item não encontrado");
        }
        repository.deleteById(id);
    }

    private EstoqueDTO toDTO(Estoque estoque) {
        EstoqueDTO dto = new EstoqueDTO();
        dto.setId(estoque.getId());
        dto.setIdLivro(estoque.getLivro().getLivId());
        dto.setTituloLivro(estoque.getLivro().getLivTitulo());
        dto.setDataEntrada(estoque.getDataEntrada());
        dto.setFornecedor(estoque.getFornecedor());
        dto.setQuantidade(estoque.getQuantidade());
        dto.setValorDeCusto(estoque.getValorDeCusto());
        return dto;
    }
    public EstoqueDTO entradaEstoque(Long id, int quantidade) {
        Estoque estoque = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        return toDTO(repository.save(estoque));
    }


    public void saidaEstoque(Long livroId, int quantidadeNecessaria) {
        List<Estoque> estoques = repository.findByLivro_LivIdOrderByIdAsc(livroId);

        int restante = quantidadeNecessaria;

        for (Estoque estoque : estoques) {
            if (restante <= 0) break;

            int disponivel = estoque.getQuantidade();
            int reduzir = Math.min(disponivel, restante);

            estoque.setQuantidade(disponivel - reduzir);
            repository.save(estoque);

            restante -= reduzir;
        }

        if (restante > 0) {
            throw new RuntimeException("Estoque insuficiente para o livro " + livroId);
        }
    }


    public List<EstoqueDTO> totalQuantidadePorLivro() {
        List<Object[]> resultados = repository.totalQuantidadePorLivro();

        List<EstoqueDTO> estoqueDTOList = new ArrayList<>();

        for (Object[] resultado : resultados) {
            Long livroId = (Long) resultado[0];
            Integer quantidadeTotal = (Integer) resultado[1];

            EstoqueDTO dto = new EstoqueDTO();
            dto.setIdLivro(livroId);
            dto.setQuantidade(quantidadeTotal);

            estoqueDTOList.add(dto);
        }

        return estoqueDTOList;
    }

}
