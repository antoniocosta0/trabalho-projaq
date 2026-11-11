package com.pucrs.cadastro.infra.adapters.in.rest;

import com.pucrs.cadastro.domain.model.Aeroporto;
import com.pucrs.cadastro.domain.ports.in.MalhaAereaUseCase;
import com.pucrs.cadastro.infra.adapters.in.rest.dto.AeroportoRequest;
import com.pucrs.cadastro.infra.adapters.in.rest.dto.AeroportoResponse;
import com.pucrs.cadastro.infra.adapters.in.rest.dto.VagasResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/aeroportos")
@Tag(name = "Aeroportos")
public class AeroportoController {

    private final MalhaAereaUseCase malhaAerea;

    public AeroportoController(MalhaAereaUseCase malhaAerea) {
        this.malhaAerea = malhaAerea;
    }

    @Operation(summary = "Listar aeroportos")
    @GetMapping
    public List<AeroportoResponse> listar() {
        return malhaAerea.listarAeroportos().stream().map(AeroportoResponse::de).toList();
    }

    @Operation(summary = "Consultar aeroporto", description = "404 se o código não existe.")
    @GetMapping("/{codigo}")
    public AeroportoResponse buscar(@PathVariable String codigo) {
        return AeroportoResponse.de(malhaAerea.buscarAeroporto(codigo));
    }

    @Operation(summary = "Vagas cadastradas do aeroporto", description = "404 se o código não existe.")
    @GetMapping("/{codigo}/vagas")
    public VagasResponse vagas(@PathVariable String codigo) {
        return VagasResponse.de(malhaAerea.buscarAeroporto(codigo));
    }

    @Operation(summary = "Cadastrar aeroporto",
            description = "Cria as vagas de 1 até quantidadeVagas. 409 se o código já existe.")
    @PostMapping
    public ResponseEntity<AeroportoResponse> cadastrar(@Valid @RequestBody AeroportoRequest req) {
        Aeroporto aeroporto = malhaAerea.cadastrarAeroporto(req.codigo(), req.nome(), req.quantidadeVagas());
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
                .buildAndExpand(aeroporto.getCodigo()).toUri()).body(AeroportoResponse.de(aeroporto));
    }
}
