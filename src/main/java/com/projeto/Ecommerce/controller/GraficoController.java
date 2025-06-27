package com.projeto.Ecommerce.controller;


import com.projeto.Ecommerce.dto.PedidoResumoDTO;
import com.projeto.Ecommerce.service.GraficoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/site")
public class GraficoController {

    private final GraficoService graficoService;

    public GraficoController(GraficoService graficoService) {
        this.graficoService = graficoService;
    }

    @GetMapping("/resumo")
    public List<PedidoResumoDTO> listarResumoPedidosEntregues() {
        return graficoService.listarPedidosEntregues();
    }
}
