package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeronaveUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarPilotoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeronavesUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPilotosUC;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrotaController {

    private final ConsultarPilotosUC consultarPilotosUC;
    private final ConsultarAeronavesUC consultarAeronavesUC;
    private final CadastrarPilotoUC cadastrarPilotoUC;
    private final CadastrarAeronaveUC cadastrarAeronaveUC;

    public FrotaController(ConsultarPilotosUC consultarPilotosUC, ConsultarAeronavesUC consultarAeronavesUC,
                           CadastrarPilotoUC cadastrarPilotoUC, CadastrarAeronaveUC cadastrarAeronaveUC) {
        this.consultarPilotosUC = consultarPilotosUC;
        this.consultarAeronavesUC = consultarAeronavesUC;
        this.cadastrarPilotoUC = cadastrarPilotoUC;
        this.cadastrarAeronaveUC = cadastrarAeronaveUC;
    }

    @GetMapping("/pilotos")
    public List<PilotoResponseDTO> listarPilotos() {
        return consultarPilotosUC.executar().stream().map(PilotoResponseDTO::de).toList();
    }

    @GetMapping("/aeronaves")
    public List<AeronaveResponseDTO> listarAeronaves() {
        return consultarAeronavesUC.executar().stream().map(AeronaveResponseDTO::de).toList();
    }

    @PostMapping("/pilotos")
    public ResponseEntity<PilotoResponseDTO> cadastrarPiloto(@Valid @RequestBody PilotoRequestDTO req) {
        Piloto piloto = cadastrarPilotoUC.executar(req.nome());
        return ResponseEntity.created(URI.create("/pilotos/" + piloto.getId())).body(PilotoResponseDTO.de(piloto));
    }

    // O controller não conhece as subclasses de Aeronave: repassa os dados e a
    // FabricaDeAeronaves, no domínio, escolhe a categoria.
    @PostMapping("/aeronaves")
    public ResponseEntity<AeronaveResponseDTO> cadastrarAeronave(@Valid @RequestBody AeronaveRequestDTO req) {
        Aeronave aeronave = cadastrarAeronaveUC.executar(req.paraDados());
        return ResponseEntity.created(URI.create("/aeronaves/" + aeronave.getPrefixo()))
                .body(AeronaveResponseDTO.de(aeronave));
    }
}
