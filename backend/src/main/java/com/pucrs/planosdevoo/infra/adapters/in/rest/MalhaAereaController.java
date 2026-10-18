package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroportoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroviaUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroportosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroviasUC;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MalhaAereaController {

    private final ConsultarAeroportosUC consultarAeroportosUC;
    private final ConsultarAeroviasUC consultarAeroviasUC;
    private final CadastrarAeroportoUC cadastrarAeroportoUC;
    private final CadastrarAeroviaUC cadastrarAeroviaUC;

    public MalhaAereaController(ConsultarAeroportosUC consultarAeroportosUC, ConsultarAeroviasUC consultarAeroviasUC,
                                CadastrarAeroportoUC cadastrarAeroportoUC, CadastrarAeroviaUC cadastrarAeroviaUC) {
        this.consultarAeroportosUC = consultarAeroportosUC;
        this.consultarAeroviasUC = consultarAeroviasUC;
        this.cadastrarAeroportoUC = cadastrarAeroportoUC;
        this.cadastrarAeroviaUC = cadastrarAeroviaUC;
    }

    @GetMapping("/aeroportos")
    public List<AeroportoResponseDTO> listarAeroportos() {
        return consultarAeroportosUC.executar().stream().map(AeroportoResponseDTO::de).toList();
    }

    @GetMapping("/aerovias")
    public List<AeroviaResponseDTO> listarAerovias() {
        return consultarAeroviasUC.executar().stream().map(AeroviaResponseDTO::de).toList();
    }

    @PostMapping("/aeroportos")
    public ResponseEntity<AeroportoResponseDTO> cadastrarAeroporto(@Valid @RequestBody AeroportoRequestDTO req) {
        Aeroporto aeroporto = cadastrarAeroportoUC.executar(req.codigo(), req.nome(), req.quantidadeVagas());
        return ResponseEntity.created(URI.create("/aeroportos/" + aeroporto.getCodigo()))
                .body(AeroportoResponseDTO.de(aeroporto));
    }

    // Aerovia só tem POST: não há PUT nem DELETE, porque ela é imutável.
    @PostMapping("/aerovias")
    public ResponseEntity<AeroviaResponseDTO> cadastrarAerovia(@Valid @RequestBody AeroviaRequestDTO req) {
        Aerovia aerovia = cadastrarAeroviaUC.executar(req.id(), req.origem(), req.destino(), req.distanciaKm());
        return ResponseEntity.created(URI.create("/aerovias/" + aerovia.getId()))
                .body(AeroviaResponseDTO.de(aerovia));
    }
}
