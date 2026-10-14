package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroportosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroviasUC;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MalhaAereaController {

    private final ConsultarAeroportosUC consultarAeroportosUC;
    private final ConsultarAeroviasUC consultarAeroviasUC;

    public MalhaAereaController(ConsultarAeroportosUC consultarAeroportosUC, ConsultarAeroviasUC consultarAeroviasUC) {
        this.consultarAeroportosUC = consultarAeroportosUC;
        this.consultarAeroviasUC = consultarAeroviasUC;
    }

    @GetMapping("/aeroportos")
    public List<AeroportoResponseDTO> listarAeroportos() {
        return consultarAeroportosUC.executar().stream().map(AeroportoResponseDTO::de).toList();
    }

    @GetMapping("/aerovias")
    public List<AeroviaResponseDTO> listarAerovias() {
        return consultarAeroviasUC.executar().stream().map(AeroviaResponseDTO::de).toList();
    }
}
