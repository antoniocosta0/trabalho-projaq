package com.pucrs.cadastro.infra.adapters.in.rest;

import com.pucrs.cadastro.domain.model.Aerovia;
import com.pucrs.cadastro.domain.ports.in.MalhaAereaUseCase;
import com.pucrs.cadastro.infra.adapters.in.rest.dto.AeroviaRequest;
import com.pucrs.cadastro.infra.adapters.in.rest.dto.AeroviaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/aerovias")
@Tag(name = "Aerovias", description = "Aerovias são orientadas e não podem ser alteradas nem excluídas.")
public class AeroviaController {

    private final MalhaAereaUseCase malhaAerea;

    public AeroviaController(MalhaAereaUseCase malhaAerea) {
        this.malhaAerea = malhaAerea;
    }

    @Operation(summary = "Listar aerovias", description = "Filtra por origem e/ou destino, se informados.")
    @GetMapping
    public List<AeroviaResponse> listar(@RequestParam(required = false) String origem,
                                        @RequestParam(required = false) String destino) {
        return malhaAerea.listarAerovias(origem, destino).stream().map(AeroviaResponse::de).toList();
    }

    @Operation(summary = "Consultar aerovia", description = "404 se o id não existe.")
    @GetMapping("/{id}")
    public AeroviaResponse buscar(@PathVariable String id) {
        return AeroviaResponse.de(malhaAerea.buscarAerovia(id));
    }

    @Operation(summary = "Cadastrar aerovia",
            description = "404 se origem ou destino não existem; 409 se o id já existe.")
    @PostMapping
    public ResponseEntity<AeroviaResponse> cadastrar(@Valid @RequestBody AeroviaRequest req) {
        Aerovia aerovia = malhaAerea.cadastrarAerovia(req.id(), req.origem(), req.destino(), req.distanciaKm());
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(aerovia.getId()).toUri()).body(AeroviaResponse.de(aerovia));
    }
}
