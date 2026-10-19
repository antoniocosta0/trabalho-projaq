package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAltitudesDisponiveisUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarVagasDisponiveisUC;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// UC07 e UC08: só consultam. O período é [inicio, fim) e o recurso só aparece
// como disponível se estiver livre em todos os slots dele.
@RestController
@Tag(name = "Disponibilidade", description = "Consultas de altitude e vaga livres num período. "
        + "Só consultam: nenhuma delas reserva coisa alguma. O período é [início, fim) e o recurso só "
        + "aparece como livre se estiver livre em TODOS os slots de uma hora que o período cobre.")
public class DisponibilidadeController {

    private final ConsultarAltitudesDisponiveisUC consultarAltitudesUC;
    private final ConsultarVagasDisponiveisUC consultarVagasUC;

    public DisponibilidadeController(ConsultarAltitudesDisponiveisUC consultarAltitudesUC,
                                     ConsultarVagasDisponiveisUC consultarVagasUC) {
        this.consultarAltitudesUC = consultarAltitudesUC;
        this.consultarVagasUC = consultarVagasUC;
    }

    @Operation(summary = "Consultar altitudes disponíveis numa aerovia (UC07)",
            description = "Devolve, das 11 altitudes possíveis (25.000 a 35.000 pés, de 1.000 em 1.000), "
                    + "as que estão livres em todos os slots do período. Exemplo com ocupação, na data de "
                    + "referência: `R2` entre 21:00 e 23:00 — a altitude de 31.000 está reservada por um "
                    + "plano da carga inicial e não aparece na lista.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Altitudes livres no período"),
            @ApiResponse(responseCode = "404", description = "Aerovia não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Período inválido: fim anterior ou igual ao início",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/aerovias/{id}/altitudes-disponiveis")
    public AltitudesDisponiveisResponseDTO altitudesDisponiveis(
            @Parameter(description = "Identificador da aerovia", example = "R2") @PathVariable String id,
            @Parameter(description = "Início do período, ISO-8601 sem fuso", example = "2026-10-19T21:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fim do período, exclusivo", example = "2026-10-19T23:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return new AltitudesDisponiveisResponseDTO(id, inicio, fim, consultarAltitudesUC.executar(id, inicio, fim));
    }

    @Operation(summary = "Consultar vagas disponíveis num aeroporto (UC08)",
            description = "Devolve as vagas do aeroporto livres em todos os slots do período. A vaga fica "
                    + "ocupada da chegada até o fim da permanência, e um pouso de 60 minutos a partir das "
                    + "08:26 ocupa dois slots: o das 08:00 e o das 09:00.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vagas livres no período"),
            @ApiResponse(responseCode = "404", description = "Aeroporto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Período inválido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/aeroportos/{codigo}/vagas-disponiveis")
    public VagasDisponiveisResponseDTO vagasDisponiveis(
            @Parameter(description = "Código do aeroporto", example = "FLN") @PathVariable String codigo,
            @Parameter(description = "Início do período, ISO-8601 sem fuso", example = "2026-10-19T08:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Fim do período, exclusivo", example = "2026-10-19T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return new VagasDisponiveisResponseDTO(codigo, inicio, fim, consultarVagasUC.executar(codigo, inicio, fim));
    }
}
