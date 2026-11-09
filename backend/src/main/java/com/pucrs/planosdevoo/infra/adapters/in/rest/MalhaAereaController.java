package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.in.MalhaAereaUseCase;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.AeroportoRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.AeroportoResponse;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.AeroviaRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.AeroviaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Malha aérea")
public class MalhaAereaController {

    private final MalhaAereaUseCase malhaAerea;

    public MalhaAereaController(MalhaAereaUseCase malhaAerea) {
        this.malhaAerea = malhaAerea;
    }

    @Operation(summary = "Listar aeroportos")
    @GetMapping("/aeroportos")
    public List<AeroportoResponse> listarAeroportos() {
        return malhaAerea.listarAeroportos().stream().map(AeroportoResponse::de).toList();
    }

    @Operation(summary = "Listar aerovias")
    @GetMapping("/aerovias")
    public List<AeroviaResponse> listarAerovias() {
        return malhaAerea.listarAerovias().stream().map(AeroviaResponse::de).toList();
    }

    @Operation(summary = "Cadastrar aeroporto", description = "409 se o código já existe.")
    @PostMapping("/aeroportos")
    public ResponseEntity<AeroportoResponse> cadastrarAeroporto(@Valid @RequestBody AeroportoRequest req) {
        Aeroporto aeroporto = malhaAerea.cadastrarAeroporto(req.codigo(), req.nome(), req.quantidadeVagas());
        return ResponseEntity.created(URI.create("/aeroportos/" + aeroporto.getCodigo()))
                .body(AeroportoResponse.de(aeroporto));
    }

    @Operation(summary = "Cadastrar aerovia",
            description = "Aerovia não pode ser alterada nem excluída: 409 se o id já existe, 404 se um aeroporto não existe.")
    @PostMapping("/aerovias")
    public ResponseEntity<AeroviaResponse> cadastrarAerovia(@Valid @RequestBody AeroviaRequest req) {
        Aerovia aerovia = malhaAerea.cadastrarAerovia(req.id(), req.origem(), req.destino(), req.distanciaKm());
        return ResponseEntity.created(URI.create("/aerovias/" + aerovia.getId())).body(AeroviaResponse.de(aerovia));
    }
}
