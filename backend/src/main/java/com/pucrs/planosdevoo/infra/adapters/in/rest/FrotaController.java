package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeronavesUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPilotosUC;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrotaController {

    private final ConsultarPilotosUC consultarPilotosUC;
    private final ConsultarAeronavesUC consultarAeronavesUC;

    public FrotaController(ConsultarPilotosUC consultarPilotosUC, ConsultarAeronavesUC consultarAeronavesUC) {
        this.consultarPilotosUC = consultarPilotosUC;
        this.consultarAeronavesUC = consultarAeronavesUC;
    }

    @GetMapping("/pilotos")
    public List<PilotoResponseDTO> listarPilotos() {
        return consultarPilotosUC.executar().stream().map(PilotoResponseDTO::de).toList();
    }

    @GetMapping("/aeronaves")
    public List<AeronaveResponseDTO> listarAeronaves() {
        return consultarAeronavesUC.executar().stream().map(AeronaveResponseDTO::de).toList();
    }
}
