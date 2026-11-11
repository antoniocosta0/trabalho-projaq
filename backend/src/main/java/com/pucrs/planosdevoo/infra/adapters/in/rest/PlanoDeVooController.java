package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.in.PlanoDeVooUseCase;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.PlanoRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.PlanoResponse;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.TrechoRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.ValidacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planos")
@Tag(name = "Planos de voo", description = "Exige o cabeçalho X-Usuario (401 sem ele). Cada usuário só vê "
        + "e altera os próprios planos (403). Só planos EM_EDICAO podem ser modificados (409).")
public class PlanoDeVooController {

    private final PlanoDeVooUseCase planos;

    public PlanoDeVooController(PlanoDeVooUseCase planos) {
        this.planos = planos;
    }

    @Operation(summary = "Criar plano", description = "O plano nasce EM_EDICAO e sem trechos.")
    @PostMapping
    public ResponseEntity<PlanoResponse> criar(@RequestHeader("X-Usuario") String usuario,
                                               @Valid @RequestBody PlanoRequest req) {
        PlanoDeVoo plano = planos.criar(usuario, req.paraComando());
        return ResponseEntity.created(Localizacao.doCriado(plano.getId())).body(PlanoResponse.de(plano));
    }

    @Operation(summary = "Alterar piloto, aeronave, origem, destino ou partida",
            description = "Os trechos continuam; trocar a aeronave recalcula as chegadas.")
    @PutMapping("/{id}")
    public PlanoResponse alterar(@RequestHeader("X-Usuario") String usuario,
                                 @PathVariable Long id, @Valid @RequestBody PlanoRequest req) {
        return PlanoResponse.de(planos.alterar(usuario, id, req.paraComando()));
    }

    @Operation(summary = "Consultar plano")
    @GetMapping("/{id}")
    public PlanoResponse buscar(@RequestHeader("X-Usuario") String usuario, @PathVariable Long id) {
        return PlanoResponse.de(planos.buscar(usuario, id));
    }

    @Operation(summary = "Listar planos", description = "Sem situacao, lista todos.")
    @GetMapping
    public List<PlanoResponse> listar(@RequestHeader("X-Usuario") String usuario,
                                      @RequestParam(required = false) SituacaoPlano situacao) {
        return planos.listar(usuario, situacao).stream().map(PlanoResponse::de).toList();
    }

    @Operation(summary = "Adicionar trecho ao fim do percurso",
            description = "numeroVaga e permanenciaMinutos juntos indicam pouso; sem eles é sobrevoo. "
                    + "A chegada é calculada pelo sistema. Indicar vaga não reserva.")
    @PostMapping("/{id}/trechos")
    public ResponseEntity<PlanoResponse> adicionarTrecho(@RequestHeader("X-Usuario") String usuario,
                                                         @PathVariable Long id, @Valid @RequestBody TrechoRequest req) {
        PlanoDeVoo plano = planos.adicionarTrecho(usuario, id, req.paraComando());
        return ResponseEntity.status(HttpStatus.CREATED).body(PlanoResponse.de(plano));
    }

    @Operation(summary = "Alterar trecho")
    @PutMapping("/{id}/trechos/{ordem}")
    public PlanoResponse alterarTrecho(@RequestHeader("X-Usuario") String usuario,
                                       @PathVariable Long id, @PathVariable int ordem,
                                       @Valid @RequestBody TrechoRequest req) {
        return PlanoResponse.de(planos.alterarTrecho(usuario, id, ordem, req.paraComando()));
    }

    @Operation(summary = "Remover trecho", description = "Os trechos seguintes são renumerados.")
    @DeleteMapping("/{id}/trechos/{ordem}")
    public PlanoResponse removerTrecho(@RequestHeader("X-Usuario") String usuario,
                                       @PathVariable Long id, @PathVariable int ordem) {
        return PlanoResponse.de(planos.removerTrecho(usuario, id, ordem));
    }

    @Operation(summary = "Validar plano",
            description = "Não muda a situação nem reserva. Responde 200 mesmo se inválido, com a lista de problemas.")
    @PostMapping("/{id}/validacao")
    public ValidacaoResponse validar(@RequestHeader("X-Usuario") String usuario, @PathVariable Long id) {
        return ValidacaoResponse.de(id, planos.validar(usuario, id));
    }

    @Operation(summary = "Aprovar plano",
            description = "Reserva tudo ou nada. 422 com a lista de problemas se não puder aprovar.")
    @PostMapping("/{id}/aprovacao")
    public PlanoResponse aprovar(@RequestHeader("X-Usuario") String usuario, @PathVariable Long id) {
        return PlanoResponse.de(planos.aprovar(usuario, id));
    }

    @Operation(summary = "Cancelar plano aprovado",
            description = "Só antes da partida. Libera as reservas.")
    @PostMapping("/{id}/cancelamento")
    public PlanoResponse cancelar(@RequestHeader("X-Usuario") String usuario, @PathVariable Long id) {
        return PlanoResponse.de(planos.cancelar(usuario, id));
    }

    @Operation(summary = "Abandonar plano em edição")
    @PostMapping("/{id}/abandono")
    public PlanoResponse abandonar(@RequestHeader("X-Usuario") String usuario, @PathVariable Long id) {
        return PlanoResponse.de(planos.abandonar(usuario, id));
    }
}
