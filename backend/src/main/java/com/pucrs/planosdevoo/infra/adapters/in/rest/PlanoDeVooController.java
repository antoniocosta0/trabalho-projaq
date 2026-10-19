package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ListarPlanosUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.AbandonarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.AprovarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CancelarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.DefinirPercursoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.TrechoCommand;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ValidarPlanoUC;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planos")
@Tag(name = "Planos de voo", description = """
        O ciclo completo de um plano. Estados: EM_EDICAO -> APROVADO -> CANCELADO, \
        ou EM_EDICAO -> ABANDONADO. Só plano EM_EDICAO pode ser modificado; os demais \
        continuam consultáveis. Validar não altera nada; aprovar reserva tudo de uma \
        vez; cancelar libera as reservas, e só vale para plano aprovado que ainda não \
        partiu.""")
public class PlanoDeVooController {

    private final CriarPlanoUC criarPlanoUC;
    private final ConsultarPlanoUC consultarPlanoUC;
    private final ListarPlanosUC listarPlanosUC;
    private final DefinirPercursoUC definirPercursoUC;
    private final ValidarPlanoUC validarPlanoUC;
    private final AbandonarPlanoUC abandonarPlanoUC;
    private final AprovarPlanoUC aprovarPlanoUC;
    private final CancelarPlanoUC cancelarPlanoUC;

    public PlanoDeVooController(CriarPlanoUC criarPlanoUC, ConsultarPlanoUC consultarPlanoUC,
                                ListarPlanosUC listarPlanosUC, DefinirPercursoUC definirPercursoUC,
                                ValidarPlanoUC validarPlanoUC, AbandonarPlanoUC abandonarPlanoUC,
                                AprovarPlanoUC aprovarPlanoUC, CancelarPlanoUC cancelarPlanoUC) {
        this.criarPlanoUC = criarPlanoUC;
        this.consultarPlanoUC = consultarPlanoUC;
        this.listarPlanosUC = listarPlanosUC;
        this.definirPercursoUC = definirPercursoUC;
        this.validarPlanoUC = validarPlanoUC;
        this.abandonarPlanoUC = abandonarPlanoUC;
        this.aprovarPlanoUC = aprovarPlanoUC;
        this.cancelarPlanoUC = cancelarPlanoUC;
    }

    @Operation(summary = "Criar plano de voo (UC05)",
            description = "O plano nasce EM_EDICAO e sem percurso. O horário de partida é o horário de "
                    + "entrada na primeira aerovia do percurso.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plano criado, em edição, sem trechos"),
            @ApiResponse(responseCode = "400", description = "Corpo inválido: a lista de campos que faltam "
                    + "vem em `problemas`", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Piloto, aeronave, origem ou destino não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping
    public ResponseEntity<PlanoResponseDTO> criar(@Valid @RequestBody CriarPlanoRequestDTO req) {
        CriarPlanoCommand comando = new CriarPlanoCommand(req.pilotoId(), req.aeronavePrefixo(),
                req.origem(), req.destino(), req.partida());
        PlanoDeVoo plano = criarPlanoUC.executar(comando);
        return ResponseEntity.created(URI.create("/planos/" + plano.getId())).body(PlanoResponseDTO.de(plano));
    }

    @Operation(summary = "Consultar plano por identificador (UC13)",
            description = "Devolve o plano com o percurso e os horários de chegada calculados. "
                    + "Plano aprovado, cancelado ou abandonado continua consultável.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plano encontrado"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/{id}")
    public PlanoResponseDTO buscar(
            @Parameter(description = "Identificador do plano", example = "1") @PathVariable Long id) {
        return PlanoResponseDTO.de(consultarPlanoUC.executar(id));
    }

    // GET /planos lista todos; GET /planos?situacao=APROVADO filtra.
    @Operation(summary = "Consultar planos por situação (UC14)",
            description = "Sem o parâmetro, lista todos os planos. Com ele, filtra pela situação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Planos encontrados"),
            @ApiResponse(responseCode = "400", description = "Situação inválida", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping
    public List<PlanoResponseDTO> listar(
            @Parameter(description = "Situação do plano. Omita para listar todos.",
                    schema = @Schema(implementation = SituacaoPlano.class))
            @RequestParam(required = false) SituacaoPlano situacao) {
        return listarPlanosUC.executar(situacao).stream().map(PlanoResponseDTO::de).toList();
    }

    // ---------- UC06: percurso ----------

    @Operation(summary = "Adicionar trecho ao percurso (UC06)",
            description = "Acrescenta um trecho ao fim do percurso. O horário de chegada é **calculado** "
                    + "pelo sistema (distância ÷ velocidade, arredondado ao minuto), nunca informado. "
                    + "Informar `numeroVaga` e `permanenciaMinutos` significa pouso; omitir os dois "
                    + "significa sobrevoo. Indicar vaga aqui **não** reserva nada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trecho adicionado; devolve o plano inteiro"),
            @ApiResponse(responseCode = "400", description = "Corpo inválido", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Plano ou aerovia não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "O plano não está em edição", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Altitude fora da faixa de 25.000 a 35.000 pés, "
                    + "ou não múltipla de 1.000", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/{id}/trechos")
    public ResponseEntity<PlanoResponseDTO> adicionarTrecho(@PathVariable Long id,
                                                            @Valid @RequestBody TrechoRequestDTO req) {
        PlanoDeVoo plano = definirPercursoUC.adicionarTrecho(id, comandoDe(req));
        return ResponseEntity.status(HttpStatus.CREATED).body(PlanoResponseDTO.de(plano));
    }

    @Operation(summary = "Alterar um trecho do percurso (UC06)",
            description = "Substitui o trecho daquela ordem. Só em plano EM_EDICAO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trecho alterado"),
            @ApiResponse(responseCode = "404", description = "Plano, trecho ou aerovia não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "O plano não está em edição", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PutMapping("/{id}/trechos/{ordem}")
    public PlanoResponseDTO alterarTrecho(@PathVariable Long id, @PathVariable int ordem,
                                          @Valid @RequestBody TrechoRequestDTO req) {
        return PlanoResponseDTO.de(definirPercursoUC.alterarTrecho(id, ordem, comandoDe(req)));
    }

    @Operation(summary = "Remover um trecho do percurso (UC06)",
            description = "Remove o trecho e **renumera** os seguintes, para a ordem continuar 1, 2, 3...")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trecho removido"),
            @ApiResponse(responseCode = "404", description = "Plano ou trecho não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "O plano não está em edição", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @DeleteMapping("/{id}/trechos/{ordem}")
    public PlanoResponseDTO removerTrecho(@PathVariable Long id, @PathVariable int ordem) {
        return PlanoResponseDTO.de(definirPercursoUC.removerTrecho(id, ordem));
    }

    // ---------- UC09: validar ----------

    // Só confere: devolve os problemas, não muda a situação e não reserva nada.
    @Operation(summary = "Validar o plano (UC09)",
            description = "Confere continuidade do percurso, horários contra a duração calculada, pouso "
                    + "obrigatório no destino, chegada no mesmo dia, regra da categoria da aeronave, "
                    + "autonomia (85% da nominal) e disponibilidade de altitude e vaga. "
                    + "**Não altera a situação do plano e não cria reserva nenhuma** — devolve 200 mesmo "
                    + "quando o plano é inválido, com `valido: false` e a lista de problemas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado da validação, válido ou não"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "O plano não está em edição", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/{id}/validacao")
    public ValidacaoResponseDTO validar(@PathVariable Long id) {
        return ValidacaoResponseDTO.de(id, validarPlanoUC.executar(id));
    }

    // ---------- UC10: aprovar ----------

    // Integral: reserva tudo e aprova, ou recusa com a lista de problemas e não muda nada.
    @Operation(summary = "Aprovar o plano (UC10)",
            description = "Reconfere tudo e, numa única transação, grava a situação APROVADO e **todas** as "
                    + "reservas: uma por slot de aerovia e uma por slot de vaga. É tudo ou nada — uma "
                    + "recusa não deixa reserva parcial e o plano continua EM_EDICAO. "
                    + "Um voo cuja partida já ocorreu não pode ser aprovado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plano aprovado e recursos reservados"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "O plano não está em edição, ou outra aprovação "
                    + "simultânea reservou o mesmo recurso primeiro", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Plano inválido ou em conflito: a lista completa "
                    + "dos motivos vem em `problemas`", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/{id}/aprovacao")
    public PlanoResponseDTO aprovar(@PathVariable Long id) {
        return PlanoResponseDTO.de(aprovarPlanoUC.executar(id));
    }

    // ---------- UC12: cancelar ----------

    @Operation(summary = "Cancelar plano aprovado (UC12)",
            description = "Só plano APROVADO **cuja partida ainda não ocorreu**. Libera todas as reservas "
                    + "de aerovia e de vaga. Um voo que já partiu foi executado: continua consultável, "
                    + "mas não é cancelável.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plano cancelado e reservas liberadas"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "O plano não está aprovado, ou a partida já "
                    + "ocorreu", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/{id}/cancelamento")
    public PlanoResponseDTO cancelar(@PathVariable Long id) {
        return PlanoResponseDTO.de(cancelarPlanoUC.executar(id));
    }

    // ---------- UC11: abandonar ----------

    @Operation(summary = "Abandonar plano em edição (UC11)",
            description = "Só plano EM_EDICAO. Não há reserva a liberar, porque plano em edição nunca "
                    + "reservou nada. O plano continua consultável depois de abandonado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plano abandonado"),
            @ApiResponse(responseCode = "404", description = "Plano não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "O plano não está em edição", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/{id}/abandono")
    public PlanoResponseDTO abandonar(@PathVariable Long id) {
        return PlanoResponseDTO.de(abandonarPlanoUC.executar(id));
    }

    private static TrechoCommand comandoDe(TrechoRequestDTO req) {
        return new TrechoCommand(req.aeroviaId(), req.altitude(), req.horarioEntrada(),
                req.numeroVaga(), req.permanenciaMinutos());
    }
}
