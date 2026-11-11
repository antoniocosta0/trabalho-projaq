package com.pucrs.planosdevoo.infra.adapters.out.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.pucrs.planosdevoo.domain.exceptions.MalhaAereaIndisponivelException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class MalhaAereaHttpAdapterTest {

    private MockRestServiceServer cadastro;
    private MalhaAereaHttpAdapter adapter;

    @BeforeEach
    void preparar() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://cadastro");
        cadastro = MockRestServiceServer.bindTo(builder).build();
        adapter = new MalhaAereaHttpAdapter(builder.build());
    }

    @Test
    void converteAAeroviaRecebida() {
        cadastro.expect(requestTo("http://cadastro/aerovias/R1")).andRespond(withSuccess(
                "{\"id\":\"R1\",\"origem\":\"POA\",\"destino\":\"FLN\",\"distanciaKm\":363.0}",
                MediaType.APPLICATION_JSON));

        assertEquals("FLN", adapter.buscarAerovia("R1").orElseThrow().getDestino());
    }

    @Test
    void recursoInexistenteViraVazio() {
        cadastro.expect(requestTo("http://cadastro/aeroportos/XXX/vagas")).andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertTrue(adapter.buscarAeroporto("XXX").isEmpty());
    }

    @Test
    void erroDoCadastroViraIndisponivel() {
        cadastro.expect(requestTo("http://cadastro/aerovias/R1")).andRespond(withServerError());

        assertThrows(MalhaAereaIndisponivelException.class, () -> adapter.buscarAerovia("R1"));
    }

    @Test
    void cadastroForaDoArViraIndisponivel() {
        MalhaAereaHttpAdapter semCadastro = new MalhaAereaHttpAdapter(RestClient.create("http://localhost:1"));

        assertThrows(MalhaAereaIndisponivelException.class, () -> semCadastro.buscarAeroporto("POA"));
    }
}
