package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAltitudesDisponiveisUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarVagasDisponiveisUC;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// UC07 e UC08: só consultam. O período é [inicio, fim) e o recurso só aparece
// como disponível se estiver livre em todos os slots dele.
@RestController
public class DisponibilidadeController {

    private final ConsultarAltitudesDisponiveisUC consultarAltitudesUC;
    private final ConsultarVagasDisponiveisUC consultarVagasUC;

    public DisponibilidadeController(ConsultarAltitudesDisponiveisUC consultarAltitudesUC,
                                     ConsultarVagasDisponiveisUC consultarVagasUC) {
        this.consultarAltitudesUC = consultarAltitudesUC;
        this.consultarVagasUC = consultarVagasUC;
    }

    @GetMapping("/aerovias/{id}/altitudes-disponiveis")
    public AltitudesDisponiveisResponseDTO altitudesDisponiveis(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return new AltitudesDisponiveisResponseDTO(id, inicio, fim, consultarAltitudesUC.executar(id, inicio, fim));
    }

    @GetMapping("/aeroportos/{codigo}/vagas-disponiveis")
    public VagasDisponiveisResponseDTO vagasDisponiveis(
            @PathVariable String codigo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return new VagasDisponiveisResponseDTO(codigo, inicio, fim, consultarVagasUC.executar(codigo, inicio, fim));
    }
}
