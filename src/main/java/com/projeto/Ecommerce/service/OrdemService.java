package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.*;
import com.projeto.Ecommerce.model.*;
import com.projeto.Ecommerce.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdemService {

    private final OrdemRepository ordemRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final LivroRepository livroRepository;
    private final OrdemLivroRepository ordemLivroRepository;
    private final PagamentoRepository pagamentoRepository;
    private final CartaoRepository cartaoRepository;
    private final CupomRepository cupomRepository;

    public OrdemService(
            OrdemRepository ordemRepository,
            ClienteRepository clienteRepository,
            EnderecoRepository enderecoRepository,
            LivroRepository livroRepository,
            OrdemLivroRepository ordemLivroRepository,
            PagamentoRepository pagamentoRepository,
            CartaoRepository cartaoRepository,
            CupomRepository cupomRepository
    ) {
        this.ordemRepository = ordemRepository;
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.livroRepository = livroRepository;
        this.ordemLivroRepository = ordemLivroRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.cartaoRepository = cartaoRepository;
        this.cupomRepository = cupomRepository;
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

    public List<OrdemResumoDTO> buscarOrdens(String nomeCliente, String tituloLivro, String status,
                                             LocalDate dataInicio, LocalDate dataFim,
                                             BigDecimal valorTotal, Long numeroPedido) {
        List<Ordem> ordens = ordemRepository.buscarOrdensComFiltros(
                nomeCliente, tituloLivro, status, dataInicio, dataFim, valorTotal, numeroPedido
        );

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
    public Optional<Ordem> buscarOrdemPorId(Long idOrdem) {
        return ordemRepository.findById(idOrdem); // Busca a ordem pelo ID
    }

    @Transactional
    public void solicitarTroca(TrocaRequestDTO dto) {
        if (dto.getLivrosParaTroca() == null || dto.getLivrosParaTroca().isEmpty()) {
            throw new RuntimeException("A lista de livros para troca não pode ser nula ou vazia");
        }

        Ordem ordemOriginal = ordemRepository.buscarOrdemComLivrosPorId(dto.getOrdemOriginalId());

        if (ordemOriginal == null) {
            throw new RuntimeException("Ordem original não encontrada");
        }

        List<OrdemLivro> livrosOriginais = ordemOriginal.getLivros();

        List<OrdemLivro> livrosParaTroca = livrosOriginais.stream()
                .filter(ol -> dto.getLivrosParaTroca().stream()
                        .anyMatch(troca -> troca.getLivroId().equals(ol.getLivro().getLivId()) &&
                                troca.getQuantidade() <= ol.getQuantidade()))
                .toList();

        if (livrosParaTroca.isEmpty()) {
            throw new RuntimeException("Nenhum livro válido para troca encontrado na ordem");
        }

        Ordem novaOrdem = new Ordem();
        novaOrdem.setCliente(ordemOriginal.getCliente());
        novaOrdem.setEndereco(ordemOriginal.getEndereco());
        novaOrdem.setData(LocalDate.now());
        novaOrdem.setStatus("TROCA SOLICITADA");
        novaOrdem.setPrecoTotal(BigDecimal.ZERO);
        novaOrdem = ordemRepository.save(novaOrdem);  // Salve a nova ordem primeiro

        BigDecimal total = BigDecimal.ZERO;

        for (LivroTrocaDTO livroTroca : dto.getLivrosParaTroca()) {
            OrdemLivro olOriginal = livrosOriginais.stream()
                    .filter(ol -> ol.getLivro().getLivId().equals(livroTroca.getLivroId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado na ordem original"));

            int quantidadeTroca = livroTroca.getQuantidade();
            if (quantidadeTroca > olOriginal.getQuantidade()) {
                throw new RuntimeException("Quantidade solicitada para troca excede a quantidade da ordem original");
            }

            OrdemLivro novoOl = new OrdemLivro();
            novoOl.setOrdem(novaOrdem);
            novoOl.setLivro(olOriginal.getLivro());
            novoOl.setQuantidade(quantidadeTroca);
            novoOl.setPreco(olOriginal.getPreco());
            ordemLivroRepository.save(novoOl);

            total = total.add(olOriginal.getPreco().multiply(BigDecimal.valueOf(quantidadeTroca)));

            if (quantidadeTroca == olOriginal.getQuantidade()) {
                ordemLivroRepository.delete(olOriginal);
                ordemOriginal.getLivros().remove(olOriginal);  // EVITA ObjectDeletedException
            } else {
                olOriginal.setQuantidade(olOriginal.getQuantidade() - quantidadeTroca);
                ordemLivroRepository.save(olOriginal);
            }
        }

        novaOrdem.setPrecoTotal(total);
        novaOrdem = ordemRepository.save(novaOrdem); // Re-salvando a nova ordem para garantir a atualização do preço total

        // Atualizar a ordem original após possíveis deleções
        ordemOriginal.getLivros().removeIf(ol -> ol.getQuantidade() == 0);

        // Apagar pagamentos associados à ordem original
        List<Pagamento> pagamentos = pagamentoRepository.findByOrdem_Id(ordemOriginal.getId());
        if (pagamentos != null && !pagamentos.isEmpty()) {
            pagamentoRepository.deleteAll(pagamentos);  // Apaga os pagamentos relacionados à ordem
        }

        if (ordemOriginal.getLivros().isEmpty()) {
            ordemRepository.delete(ordemOriginal);  // Todos os livros foram trocados, então apaga a ordem
        } else {
            ordemRepository.save(ordemOriginal);  // Ainda há livros na ordem original
        }

        // Não criamos mais o cupom aqui - só quando a troca for autorizada
    }

    /*
     * Método para atualizar o status de uma ordem de troca
     * @param ordemId ID da ordem de troca
     * @param novoStatus novo status a ser aplicado
     */
    @Transactional
    public void atualizarStatusTroca(Long ordemId, String novoStatus) {
        Ordem ordem = ordemRepository.findById(ordemId)
                .orElseThrow(() -> new RuntimeException("Ordem não encontrada"));

        String statusAnterior = ordem.getStatus();
        ordem.setStatus(novoStatus);
        ordemRepository.save(ordem);

        // Se o status foi alterado para "TROCA AUTORIZADA", criamos o cupom
        if ("TROCA AUTORIZADA".equals(novoStatus) && !"TROCA AUTORIZADA".equals(statusAnterior)) {
            criarCupomParaTroca(ordem);
        }
    }

    /**
     * Método privado que cria um cupom para uma ordem de troca autorizada
     * @param ordemTroca a ordem de troca que foi autorizada
     */
    private void criarCupomParaTroca(Ordem ordemTroca) {
        // Verificamos se é realmente uma ordem de troca
        if (ordemTroca == null || !ordemTroca.getStatus().equals("TROCA AUTORIZADA")) {
            return; // Não é uma troca autorizada
        }

        // Verifica se já existe cupom para esta troca
        boolean cupomExiste = cupomRepository.existsByOrigemTroca_Id(ordemTroca.getId());
        if (cupomExiste) {
            return; // Evita duplicação de cupons
        }

        // Criação do cupom de troca
        Cupom cupom = new Cupom();
        cupom.setCodigo("TROCA-" + ordemTroca.getId() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        cupom.setValor(ordemTroca.getPrecoTotal());
        cupom.setTipo(Cupom.TipoDesconto.TROCA);
        cupom.setTroca(true);
        cupom.setValidade(LocalDate.now().plusMonths(1));
        cupom.setCliente(ordemTroca.getCliente());
        cupom.setOrigemTroca(ordemTroca);

        cupomRepository.save(cupom);
    }
    }
