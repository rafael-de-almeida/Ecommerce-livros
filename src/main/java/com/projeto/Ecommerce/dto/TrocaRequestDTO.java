package com.projeto.Ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class TrocaRequestDTO {
    private Long ordemOriginalId;
    private List<LivroTrocaDTO> livrosParaTroca;
}
