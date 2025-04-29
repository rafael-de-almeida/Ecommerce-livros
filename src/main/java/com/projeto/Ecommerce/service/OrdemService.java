package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.*;
import com.projeto.Ecommerce.model.*;
import com.projeto.Ecommerce.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrdemService {

    private final OrdemRepository ordemRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final LivroRepository livroRepository;
    private final OrdemLivroRepository ordemLivroRepository;
    private final PagamentoRepository pagamentoRepository;
    private final CartaoRepository cartaoRepository;

    public OrdemService(
            OrdemRepository ordemRepository,
            ClienteRepository clienteRepository,
            EnderecoRepository enderecoRepository,
            LivroRepository livroRepository,
            OrdemLivroRepository ordemLivroRepository,
            PagamentoRepository pagamentoRepository,
            CartaoRepository cartaoRepository
    ) {
        this.ordemRepository = ordemRepository;
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.livroRepository = livroRepository;
        this.ordemLivroRepository = ordemLivroRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.cartaoRepository = cartaoRepository;
    }

    public void criarOrdem(OrdemRequestDTO dto) {
        // Buscar cliente
        Clientes cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Buscar endereço
        Enderecos endereco = enderecoRepository.findById(dto.getEnderecoId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        // Criar a ordem
        Ordem ordem = new Ordem();
        ordem.setPrecoTotal(dto.getPrecoTotal());
        ordem.setStatus(dto.getStatus());
        ordem.setData(dto.getData());
        ordem.setCliente(cliente);
        ordem.setEndereco(endereco);
        ordemRepository.save(ordem);

        // Adicionar livros
        for (OrdemLivroDTO livroDTO : dto.getLivros()) {
            Livros livro = livroRepository.findById(livroDTO.getLivroId())
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

            OrdemLivro ordemLivro = new OrdemLivro();
            ordemLivro.setOrdem(ordem);
            ordemLivro.setLivro(livro);
            ordemLivro.setQuantidade(livroDTO.getQuantidade());
            ordemLivro.setPreco(livroDTO.getPreco());
            ordemLivroRepository.save(ordemLivro);
        }

        // Adicionar pagamentos
        if (dto.getPagamentos() != null) {
            for (PagamentoDTO pagDTO : dto.getPagamentos()) {
                Cartoes cartao = cartaoRepository.findById(pagDTO.getCartaoId())
                        .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));

                Pagamento pagamento = new Pagamento();
                pagamento.setOrdem(ordem);
                pagamento.setCartao(cartao);
                pagamento.setStatus(pagDTO.getStatus());
                pagamentoRepository.save(pagamento);
            }
        }
    }

    public List<LivroResumoDTO> listarLivrosDaOrdem(Long ordemId) {
        return ordemLivroRepository.findPedidosByClienteId(ordemId);
    }

    public List<OrdemResumoDTO> buscarOrdens(String nomeCliente, String status, LocalDate dataInicio, LocalDate dataFim) {
        List<Ordem> ordens = ordemRepository.buscarOrdensComFiltros(nomeCliente, status,dataInicio,dataFim);

        return ordens.stream().map(ordem -> {
            OrdemResumoDTO dto = new OrdemResumoDTO();
            dto.setNumeroPedido(ordem.getId());
            dto.setNomeCliente(ordem.getCliente().getCliNome());
            dto.setValorTotal(ordem.getPrecoTotal());
            dto.setStatus(ordem.getStatus());
            dto.setData(ordem.getData());

            List<String> titulos = ordem.getLivros().stream()
                    .map(ol -> ol.getLivro().getLivTitulo())
                    .toList();

            dto.setLivros(titulos);
            return dto;
        }).toList();
    }
}
