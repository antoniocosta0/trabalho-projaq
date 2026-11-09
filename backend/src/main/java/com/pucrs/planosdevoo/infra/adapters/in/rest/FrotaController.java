package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.in.FrotaUseCase;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.AeronaveRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.AeronaveResponse;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.PilotoRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.PilotoResponse;
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
@Tag(name = "Pilotos e aeronaves")
public class FrotaController {

    private final FrotaUseCase frota;

    public FrotaController(FrotaUseCase frota) {
        this.frota = frota;
    }

    @Operation(summary = "Listar pilotos")
    @GetMapping("/pilotos")
    public List<PilotoResponse> listarPilotos() {
        return frota.listarPilotos().stream().map(PilotoResponse::de).toList();
    }

    @Operation(summary = "Listar aeronaves")
    @GetMapping("/aeronaves")
    public List<AeronaveResponse> listarAeronaves() {
        return frota.listarAeronaves().stream().map(AeronaveResponse::de).toList();
    }

    @Operation(summary = "Cadastrar piloto")
    @PostMapping("/pilotos")
    public ResponseEntity<PilotoResponse> cadastrarPiloto(@Valid @RequestBody PilotoRequest req) {
        Piloto piloto = frota.cadastrarPiloto(req.nome());
        return ResponseEntity.created(URI.create("/pilotos/" + piloto.getId())).body(PilotoResponse.de(piloto));
    }

    @Operation(summary = "Cadastrar aeronave",
            description = "Categoria PARTICULAR exige empresaManutencao; PASSAGEIROS exige companhiaAerea e "
                    + "capacidadePassageiros; CARGA exige companhiaAerea e capacidadeToneladas.")
    @PostMapping("/aeronaves")
    public ResponseEntity<AeronaveResponse> cadastrarAeronave(@Valid @RequestBody AeronaveRequest req) {
        Aeronave aeronave = frota.cadastrarAeronave(req.paraComando());
        return ResponseEntity.created(URI.create("/aeronaves/" + aeronave.getPrefixo()))
                .body(AeronaveResponse.de(aeronave));
    }
}
