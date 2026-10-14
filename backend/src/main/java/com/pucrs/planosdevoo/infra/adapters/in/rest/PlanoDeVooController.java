package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.PlanoDeVoo;
import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ConsultarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoCommand;
import com.pucrs.planosdevoo.domain.ports.in.usecases.CriarPlanoUC;
import com.pucrs.planosdevoo.domain.ports.in.usecases.ListarPlanosUC;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    public PlanoDeVooController(CriarPlanoUC criarPlanoUC, ConsultarPlanoUC consultarPlanoUC,
                                ListarPlanosUC listarPlanosUC) {
        this.criarPlanoUC = criarPlanoUC;
        this.consultarPlanoUC = consultarPlanoUC;
        this.listarPlanosUC = listarPlanosUC;
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
}
