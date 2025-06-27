package com.projeto.Ecommerce.service;

import com.projeto.Ecommerce.dto.*;
import com.projeto.Ecommerce.model.*;
import com.projeto.Ecommerce.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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
    private final EstoqueRepository estoqueRepository;
    private final EstoqueService estoqueService;
    private final CategoriaRepository categoriaRepository;
    public OrdemService(
            OrdemRepository ordemRepository,
            ClienteRepository clienteRepository,
            EnderecoRepository enderecoRepository,
            LivroRepository livroRepository,
            OrdemLivroRepository ordemLivroRepository,
            PagamentoRepository pagamentoRepository,
            CartaoRepository cartaoRepository,
            CupomRepository cupomRepository,
            EstoqueRepository estoqueRepository,
            EstoqueService estoqueService,
            CategoriaRepository categoriaRepository
    ) {
        this.ordemRepository = ordemRepository;
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.livroRepository = livroRepository;
        this.ordemLivroRepository = ordemLivroRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.cartaoRepository = cartaoRepository;
        this.cupomRepository = cupomRepository;
        this.estoqueRepository = estoqueRepository;
        this.estoqueService = estoqueService;
        this.categoriaRepository = categoriaRepository;
    }

    public void criarOrdem(OrdemRequestDTO dto) {
        Clientes cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Enderecos endereco = enderecoRepository.findById(dto.getEnderecoId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        Ordem ordem = new Ordem();
        ordem.setPrecoTotal(dto.getPrecoTotal());
        ordem.setStatus(dto.getStatus());
        LocalDate localDate = dto.getData();
        if (localDate != null) {
            Date date = (Date) Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            ordem.setData(date);
        } else {
            ordem.setData(null);
        }
        ordem.setCliente(cliente);
        ordem.setEndereco(endereco);
        ordemRepository.save(ordem);

        for (OrdemLivroDTO livroDTO : dto.getLivros()) {
            Livros livro = livroRepository.findById(livroDTO.getLivroId())
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

            int totalEstoque = estoqueRepository.totalEstoquePorLivro(livro.getLivId());
            if (totalEstoque < livroDTO.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o livro: " + livro.getLivTitulo());
            }

            estoqueService.saidaEstoque(livro.getLivId(), livroDTO.getQuantidade());

            OrdemLivro ordemLivro = new OrdemLivro();
            ordemLivro.setOrdem(ordem);
            ordemLivro.setLivro(livro);
            ordemLivro.setQuantidade(livroDTO.getQuantidade());
            ordemLivro.setPreco(livroDTO.getPreco());
            ordemLivroRepository.save(ordemLivro);
        }

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

    public List<OrdemResumoDTO> buscarOrdens(String nomeCliente, String tituloLivro, String categoria, String status,
                                             LocalDate dataInicio, LocalDate dataFim,
                                             BigDecimal valorTotal, Long numeroPedido) {

        List<Ordem> ordens = ordemRepository.buscarOrdensComFiltros(
                nomeCliente, tituloLivro, categoria, status, dataInicio, dataFim, valorTotal, numeroPedido
        );

        return ordens.stream().map(ordem -> {
            List<LivroQuantidadeDTO> livros = ordem.getLivros().stream()
                    .map(ol -> new LivroQuantidadeDTO(
                            ol.getLivro().getLivTitulo(),
                            ol.getQuantidade(),
                            ol.getLivro().getCategorias().stream()
                                    .map(Categorias::getNome)
                                    .collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());

            List<String> categorias = ordem.getLivros().stream()
                    .flatMap(ol -> ol.getLivro().getCategorias().stream())
                    .map(Categorias::getNome)
                    .distinct()
                    .collect(Collectors.toList());

            LocalDate dataConvertida = ordem.getData() != null
                    ? ((java.sql.Date) ordem.getData()).toLocalDate()
                    : null;

            return new OrdemResumoDTO(
                    ordem.getId(),
                    ordem.getCliente().getCliNome(),
                    livros,
                    categorias,
                    ordem.getPrecoTotal(),
                    ordem.getStatus(),
                    dataConvertida
            );
        }).collect(Collectors.toList());
    }

    public Optional<Ordem> buscarOrdemPorId(Long idOrdem) {
        return ordemRepository.findById(idOrdem);
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
        LocalDate localDate = LocalDate.now();
        Date date = (Date) Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        novaOrdem.setData(date);
        novaOrdem.setStatus("TROCA SOLICITADA");
        novaOrdem.setPrecoTotal(BigDecimal.ZERO);
        novaOrdem = ordemRepository.save(novaOrdem);

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
                ordemOriginal.getLivros().remove(olOriginal);
            } else {
                olOriginal.setQuantidade(olOriginal.getQuantidade() - quantidadeTroca);
                ordemLivroRepository.save(olOriginal);
            }
        }

        novaOrdem.setPrecoTotal(total);
        novaOrdem = ordemRepository.save(novaOrdem);

        ordemOriginal.getLivros().removeIf(ol -> ol.getQuantidade() == 0);

        List<Pagamento> pagamentos = pagamentoRepository.findByOrdem_Id(ordemOriginal.getId());
        if (pagamentos != null && !pagamentos.isEmpty()) {
            pagamentoRepository.deleteAll(pagamentos);
        }

        if (ordemOriginal.getLivros().isEmpty()) {
            ordemRepository.delete(ordemOriginal);
        } else {
            ordemRepository.save(ordemOriginal);
        }
    }
    public List<LivroResumoDTO> listarLivrosDaOrdem(Long ordemId) {
        return ordemLivroRepository.findPedidosByClienteId(ordemId);
    }

    @Transactional
    public void atualizarStatusTroca(Long ordemId, String novoStatus) {
        Ordem ordem = ordemRepository.findById(ordemId)
                .orElseThrow(() -> new RuntimeException("Ordem não encontrada"));

        String statusAnterior = ordem.getStatus();
        ordem.setStatus(novoStatus);
        ordemRepository.save(ordem);

        if ("TROCA AUTORIZADA".equals(novoStatus) && !"TROCA AUTORIZADA".equals(statusAnterior)) {
            criarCupomParaTroca(ordem);
        }
    }

    private void criarCupomParaTroca(Ordem ordemTroca) {
        if (ordemTroca == null || !ordemTroca.getStatus().equals("TROCA AUTORIZADA")) {
            return;
        }

        boolean cupomExiste = cupomRepository.existsByOrigemTroca_Id(ordemTroca.getId());
        if (cupomExiste) return;

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
    public List<LivroResumoDTO> listarLivrosDaOrdemComCategorias(Long clienteId) {
        List<LivroResumoDTO> livros = ordemLivroRepository.findPedidosByClienteId(clienteId);

        for (LivroResumoDTO livro : livros) {
            List<String> categorias = categoriaRepository.findNomesByLivroId(livro.getLivroId());
            livro.setCategorias(categorias);
        }

        return livros;
    }
}
