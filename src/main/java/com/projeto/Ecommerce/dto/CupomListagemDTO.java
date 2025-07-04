package com.projeto.Ecommerce.dto;

import com.projeto.Ecommerce.model.Cupom;
import java.math.BigDecimal;
import java.time.LocalDate;


@lombok.Getter
@lombok.Setter
public class CupomListagemDTO {

    private String codigo;
    private BigDecimal valor;
    private Cupom.TipoDesconto tipo;
    private LocalDate validade;
    private String descricao;


    public CupomListagemDTO() {
    }


    public CupomListagemDTO(String codigo, BigDecimal valor, Cupom.TipoDesconto tipo, LocalDate validade, String descricao) {
        this.codigo = codigo;
        this.valor = valor;
        this.tipo = tipo;
        this.validade = validade;
        this.descricao = descricao;
    }
}