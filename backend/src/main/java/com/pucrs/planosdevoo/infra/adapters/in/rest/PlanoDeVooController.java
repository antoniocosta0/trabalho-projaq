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

    @PostMapping
    public ResponseEntity<PlanoResponseDTO> criar(@Valid @RequestBody CriarPlanoRequestDTO req) {
        CriarPlanoCommand comando = new CriarPlanoCommand(req.pilotoId(), req.aeronavePrefixo(),
                req.origem(), req.destino(), req.partida());
        PlanoDeVoo plano = criarPlanoUC.executar(comando);
        return ResponseEntity.created(URI.create("/planos/" + plano.getId())).body(PlanoResponseDTO.de(plano));
    }

    @GetMapping("/{id}")
    public PlanoResponseDTO buscar(@PathVariable Long id) {
        return PlanoResponseDTO.de(consultarPlanoUC.executar(id));
    }

    // GET /planos lista todos; GET /planos?situacao=APROVADO filtra.
    @GetMapping
    public List<PlanoResponseDTO> listar(@RequestParam(required = false) SituacaoPlano situacao) {
        return listarPlanosUC.executar(situacao).stream().map(PlanoResponseDTO::de).toList();
    }

    // ---------- UC06: percurso ----------

    @PostMapping("/{id}/trechos")
    public ResponseEntity<PlanoResponseDTO> adicionarTrecho(@PathVariable Long id,
                                                            @Valid @RequestBody TrechoRequestDTO req) {
        PlanoDeVoo plano = definirPercursoUC.adicionarTrecho(id, comandoDe(req));
        return ResponseEntity.status(HttpStatus.CREATED).body(PlanoResponseDTO.de(plano));
    }

    @PutMapping("/{id}/trechos/{ordem}")
    public PlanoResponseDTO alterarTrecho(@PathVariable Long id, @PathVariable int ordem,
                                          @Valid @RequestBody TrechoRequestDTO req) {
        return PlanoResponseDTO.de(definirPercursoUC.alterarTrecho(id, ordem, comandoDe(req)));
    }

    @DeleteMapping("/{id}/trechos/{ordem}")
    public PlanoResponseDTO removerTrecho(@PathVariable Long id, @PathVariable int ordem) {
        return PlanoResponseDTO.de(definirPercursoUC.removerTrecho(id, ordem));
    }

    // ---------- UC09: validar ----------

    // Só confere: devolve os problemas, não muda a situação e não reserva nada.
    @PostMapping("/{id}/validacao")
    public ValidacaoResponseDTO validar(@PathVariable Long id) {
        return ValidacaoResponseDTO.de(id, validarPlanoUC.executar(id));
    }

    // ---------- UC10: aprovar ----------

    // Integral: reserva tudo e aprova, ou recusa com a lista de problemas e não muda nada.
    @PostMapping("/{id}/aprovacao")
    public PlanoResponseDTO aprovar(@PathVariable Long id) {
        return PlanoResponseDTO.de(aprovarPlanoUC.executar(id));
    }

    // ---------- UC12: cancelar ----------

    @PostMapping("/{id}/cancelamento")
    public PlanoResponseDTO cancelar(@PathVariable Long id) {
        return PlanoResponseDTO.de(cancelarPlanoUC.executar(id));
    }

    // ---------- UC11: abandonar ----------

    @PostMapping("/{id}/abandono")
    public PlanoResponseDTO abandonar(@PathVariable Long id) {
        return PlanoResponseDTO.de(abandonarPlanoUC.executar(id));
    }

    private static TrechoCommand comandoDe(TrechoRequestDTO req) {
        return new TrechoCommand(req.aeroviaId(), req.altitude(), req.horarioEntrada(),
                req.numeroVaga(), req.permanenciaMinutos());
    }
}
