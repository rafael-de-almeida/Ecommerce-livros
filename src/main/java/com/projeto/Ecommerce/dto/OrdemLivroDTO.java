package com.projeto.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Getter
@Setter

public class OrdemLivroDTO {
    private Integer livroId;
    private Integer quantidade;
    private BigDecimal preco;
}
