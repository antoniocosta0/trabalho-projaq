package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeronave;
import com.pucrs.planosdevoo.domain.model.Piloto;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeronaveUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarPilotoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeronavesUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPilotosUC;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ProblemDetail;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Frota", description = "Pilotos e aeronaves. Cada categoria de aeronave tem a sua regra de "
        + "operação: particular só voa a 25.000, 26.000 e 27.000 pés; passageiros a 28.000 ou mais; "
        + "carga em qualquer altitude, mas só entre 00:00 e 06:00.")
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

    @Operation(summary = "Consultar pilotos (UC03)")
    @ApiResponse(responseCode = "200", description = "Lista de pilotos")
    @GetMapping("/pilotos")
    public List<PilotoResponseDTO> listarPilotos() {
        return consultarPilotosUC.executar().stream().map(PilotoResponseDTO::de).toList();
    }

    @Operation(summary = "Consultar aeronaves (UC04)",
            description = "Lista as aeronaves com prefixo, categoria, velocidade de cruzeiro e autonomia.")
    @ApiResponse(responseCode = "200", description = "Lista de aeronaves")
    @GetMapping("/aeronaves")
    public List<AeronaveResponseDTO> listarAeronaves() {
        return consultarAeronavesUC.executar().stream().map(AeronaveResponseDTO::de).toList();
    }

    @Operation(summary = "Cadastrar piloto",
            description = "O id é gerado pelo banco, a partir de 101 — os ids 1 a 4 são da carga inicial.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Piloto cadastrado"),
            @ApiResponse(responseCode = "400", description = "Nome ausente",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/pilotos")
    public ResponseEntity<PilotoResponseDTO> cadastrarPiloto(@Valid @RequestBody PilotoRequestDTO req) {
        Piloto piloto = cadastrarPilotoUC.executar(req.nome());
        return ResponseEntity.created(URI.create("/pilotos/" + piloto.getId())).body(PilotoResponseDTO.de(piloto));
    }

    // O controller não conhece as subclasses de Aeronave: repassa os dados e a
    // FabricaDeAeronaves, no domínio, escolhe a categoria.
    @Operation(summary = "Cadastrar aeronave",
            description = "A categoria determina a subclasse criada e quais campos são obrigatórios: "
                    + "`empresaManutencao` para PARTICULAR, `companhiaAerea` e `capacidadePassageiros` "
                    + "para PASSAGEIROS, `companhiaAerea` e `capacidadeToneladas` para CARGA. "
                    + "Uma categoria nova entra como subclasse nova, sem alterar a validação do plano.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aeronave cadastrada"),
            @ApiResponse(responseCode = "400", description = "Corpo inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe aeronave com esse prefixo",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422",
                    description = "Categoria desconhecida, ou falta um campo obrigatório da categoria",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/aeronaves")
    public ResponseEntity<AeronaveResponseDTO> cadastrarAeronave(@Valid @RequestBody AeronaveRequestDTO req) {
        Aeronave aeronave = cadastrarAeronaveUC.executar(req.paraDados());
        return ResponseEntity.created(URI.create("/aeronaves/" + aeronave.getPrefixo()))
                .body(AeronaveResponseDTO.de(aeronave));
    }
}
