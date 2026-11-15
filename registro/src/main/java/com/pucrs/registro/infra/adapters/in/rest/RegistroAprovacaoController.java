package com.pucrs.registro.infra.adapters.in.rest;

import com.pucrs.registro.domain.ports.in.RegistroAprovacaoUseCase;
import com.pucrs.registro.infra.adapters.in.rest.dto.RegistroResumoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprovacoes")
@Tag(name = "Aprovações registradas")
public class RegistroAprovacaoController {

    private final RegistroAprovacaoUseCase registros;

    public RegistroAprovacaoController(RegistroAprovacaoUseCase registros) {
        this.registros = registros;
    }

    @Operation(summary = "Listar aprovações registradas")
    @GetMapping
    public List<RegistroResumoResponse> listar() {
        return registros.listar().stream().map(RegistroResumoResponse::de).toList();
    }

    @Operation(summary = "Aprovação de um plano, como foi recebida",
            description = "Devolve a mensagem de aprovação gravada. 404 se o plano não tem aprovação registrada.")
    @GetMapping("/{planoId}")
    public ResponseEntity<String> buscar(@PathVariable Long planoId) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(registros.buscarPorPlano(planoId).getConteudo());
    }
}
