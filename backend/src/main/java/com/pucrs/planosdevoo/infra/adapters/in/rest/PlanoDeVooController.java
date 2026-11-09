package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.in.PlanoDeVooUseCase;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.CriarPlanoRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.PlanoResponse;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.TrechoRequest;
import com.pucrs.planosdevoo.infra.adapters.in.rest.dto.ValidacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/planos")
@Tag(name = "Planos de voo", description = "Só planos EM_EDICAO podem ser modificados (409 nos demais).")
public class PlanoDeVooController {

    private final PlanoDeVooUseCase planos;

    public PlanoDeVooController(PlanoDeVooUseCase planos) {
        this.planos = planos;
    }

    @Operation(summary = "Criar plano", description = "O plano nasce EM_EDICAO e sem trechos.")
    @PostMapping
    public ResponseEntity<PlanoResponse> criar(@Valid @RequestBody CriarPlanoRequest req) {
        PlanoDeVoo plano = planos.criar(req.paraComando());
        return ResponseEntity.created(URI.create("/planos/" + plano.getId())).body(PlanoResponse.de(plano));
    }

    @Operation(summary = "Consultar plano")
    @GetMapping("/{id}")
    public PlanoResponse buscar(@PathVariable Long id) {
        return PlanoResponse.de(planos.buscar(id));
    }

    @Operation(summary = "Listar planos", description = "Sem situacao, lista todos.")
    @GetMapping
    public List<PlanoResponse> listar(@RequestParam(required = false) SituacaoPlano situacao) {
        return planos.listar(situacao).stream().map(PlanoResponse::de).toList();
    }

    @Operation(summary = "Adicionar trecho ao fim do percurso",
            description = "numeroVaga e permanenciaMinutos juntos indicam pouso; sem eles é sobrevoo. "
                    + "A chegada é calculada pelo sistema. Indicar vaga não reserva.")
    @PostMapping("/{id}/trechos")
    public ResponseEntity<PlanoResponse> adicionarTrecho(@PathVariable Long id, @Valid @RequestBody TrechoRequest req) {
        PlanoDeVoo plano = planos.adicionarTrecho(id, req.paraComando());
        return ResponseEntity.status(HttpStatus.CREATED).body(PlanoResponse.de(plano));
    }

    @Operation(summary = "Alterar trecho")
    @PutMapping("/{id}/trechos/{ordem}")
    public PlanoResponse alterarTrecho(@PathVariable Long id, @PathVariable int ordem,
                                       @Valid @RequestBody TrechoRequest req) {
        return PlanoResponse.de(planos.alterarTrecho(id, ordem, req.paraComando()));
    }

    @Operation(summary = "Remover trecho", description = "Os trechos seguintes são renumerados.")
    @DeleteMapping("/{id}/trechos/{ordem}")
    public PlanoResponse removerTrecho(@PathVariable Long id, @PathVariable int ordem) {
        return PlanoResponse.de(planos.removerTrecho(id, ordem));
    }

    @Operation(summary = "Validar plano",
            description = "Não muda a situação nem reserva. Responde 200 mesmo se inválido, com a lista de problemas.")
    @PostMapping("/{id}/validacao")
    public ValidacaoResponse validar(@PathVariable Long id) {
        return ValidacaoResponse.de(id, planos.validar(id));
    }

    @Operation(summary = "Aprovar plano",
            description = "Reserva tudo ou nada. 422 com a lista de problemas se não puder aprovar.")
    @PostMapping("/{id}/aprovacao")
    public PlanoResponse aprovar(@PathVariable Long id) {
        return PlanoResponse.de(planos.aprovar(id));
    }

    @Operation(summary = "Cancelar plano aprovado",
            description = "Só antes da partida. Libera as reservas.")
    @PostMapping("/{id}/cancelamento")
    public PlanoResponse cancelar(@PathVariable Long id) {
        return PlanoResponse.de(planos.cancelar(id));
    }

    @Operation(summary = "Abandonar plano em edição")
    @PostMapping("/{id}/abandono")
    public PlanoResponse abandonar(@PathVariable Long id) {
        return PlanoResponse.de(planos.abandonar(id));
    }
}
