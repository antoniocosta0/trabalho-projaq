package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.Aeroporto;
import com.pucrs.planosdevoo.domain.model.Aerovia;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroportoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CadastrarAeroviaUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroportosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarAeroviasUC;
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
@Tag(name = "Malha aérea", description = "Aeroportos e aerovias. A aerovia é orientada e imutável: "
        + "POA->FLN (R1) e FLN->POA (R8) são registros distintos, e não existe alteração nem exclusão.")
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

    @Operation(summary = "Consultar aeroportos (UC01)",
            description = "Lista os aeroportos da malha, com código, nome e quantidade de vagas. "
                    + "A malha vem do banco: aeroporto novo entra pelo POST, sem recompilar.")
    @ApiResponse(responseCode = "200", description = "Lista de aeroportos")
    @GetMapping("/aeroportos")
    public List<AeroportoResponseDTO> listarAeroportos() {
        return consultarAeroportosUC.executar().stream().map(AeroportoResponseDTO::de).toList();
    }

    @Operation(summary = "Consultar aerovias (UC02)",
            description = "Lista as aerovias com identificador, origem, destino e distância em km. "
                    + "São orientadas: o sentido inverso é outra aerovia.")
    @ApiResponse(responseCode = "200", description = "Lista de aerovias")
    @GetMapping("/aerovias")
    public List<AeroviaResponseDTO> listarAerovias() {
        return consultarAeroviasUC.executar().stream().map(AeroviaResponseDTO::de).toList();
    }

    @Operation(summary = "Cadastrar aeroporto",
            description = "Cadastra um aeroporto novo, que já pode ser usado em aerovias e planos na mesma "
                    + "execução, sem reiniciar a aplicação. Não é caso de uso do trabalho (ver ADR 0009), "
                    + "mas é exigido pelo enunciado (§2 e §8.1).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aeroporto cadastrado"),
            @ApiResponse(responseCode = "400", description = "Corpo inválido: campo obrigatório ausente",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe aeroporto com esse código",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/aeroportos")
    public ResponseEntity<AeroportoResponseDTO> cadastrarAeroporto(@Valid @RequestBody AeroportoRequestDTO req) {
        Aeroporto aeroporto = cadastrarAeroportoUC.executar(req.codigo(), req.nome(), req.quantidadeVagas());
        return ResponseEntity.created(URI.create("/aeroportos/" + aeroporto.getCodigo()))
                .body(AeroportoResponseDTO.de(aeroporto));
    }

    // Aerovia só tem POST: não há PUT nem DELETE, porque ela é imutável.
    @Operation(summary = "Cadastrar aerovia",
            description = "Cadastra uma aerovia nova entre dois aeroportos já existentes. "
                    + "**Não há alteração nem exclusão**: corrigir uma aerovia significa cadastrar outra, "
                    + "com id novo, o que preserva os planos e reservas que já a referenciam.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aerovia cadastrada"),
            @ApiResponse(responseCode = "400", description = "Corpo inválido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Aeroporto de origem ou destino não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe aerovia com esse id",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/aerovias")
    public ResponseEntity<AeroviaResponseDTO> cadastrarAerovia(@Valid @RequestBody AeroviaRequestDTO req) {
        Aerovia aerovia = cadastrarAeroviaUC.executar(req.id(), req.origem(), req.destino(), req.distanciaKm());
        return ResponseEntity.created(URI.create("/aerovias/" + aerovia.getId()))
                .body(AeroviaResponseDTO.de(aerovia));
    }
}
