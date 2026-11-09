package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.ports.in.DisponibilidadeUseCase;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.AltitudesDisponiveisResponse;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.VagasDisponiveisResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Disponibilidade", description = "Só consulta, não reserva. O período é [inicio, fim).")
public class DisponibilidadeController {

    private final DisponibilidadeUseCase disponibilidade;

    public DisponibilidadeController(DisponibilidadeUseCase disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    @Operation(summary = "Altitudes livres numa aerovia em todos os slots do período")
    @GetMapping("/aerovias/{id}/altitudes-disponiveis")
    public AltitudesDisponiveisResponse altitudesDisponiveis(
            @Parameter(example = "R2") @PathVariable String id,
            @Parameter(example = "2026-10-19T21:00:00") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(example = "2026-10-19T23:00:00") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return new AltitudesDisponiveisResponse(id, inicio, fim, disponibilidade.altitudesDisponiveis(id, inicio, fim));
    }

    @Operation(summary = "Vagas livres num aeroporto em todos os slots do período")
    @GetMapping("/aeroportos/{codigo}/vagas-disponiveis")
    public VagasDisponiveisResponse vagasDisponiveis(
            @Parameter(example = "FLN") @PathVariable String codigo,
            @Parameter(example = "2026-10-19T08:00:00") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(example = "2026-10-19T10:00:00") @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return new VagasDisponiveisResponse(codigo, inicio, fim, disponibilidade.vagasDisponiveis(codigo, inicio, fim));
    }
}
