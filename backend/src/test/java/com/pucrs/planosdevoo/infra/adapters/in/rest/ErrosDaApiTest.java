package com.pucrs.planosdevoo.infra.adapters.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

// Toda resposta de erro da API: status certo, formato Problem Detail e NENHUM
// stack trace no corpo. O enunciado avalia a API "inclusive em situações de
// sucesso e erro" (§8.4), e um stack trace na tela durante a apresentação é o
// pior jeito de descobrir que faltava um handler.
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:erros-api;DB_CLOSE_DELAY=-1",
        "app.relogio.instante-fixo=2026-10-18T12:00:00"})
class ErrosDaApiTest {

    @Autowired MockMvc mvc;

    private static final String PLANO_VALIDO = """
            {"pilotoId":1,"aeronavePrefixo":"PR-PAX","origem":"POA","destino":"FLN",
             "partida":"2026-10-19T08:00:00"}""";

    @Test
    void jsonMalformadoDevolve400SemStackTrace() throws Exception {
        mvc.perform(post("/planos").contentType(MediaType.APPLICATION_JSON).content("{\"pilotoId\":1,"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.problemas").isArray())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("trace"))));
    }

    @Test
    void dataForaDoFormatoIsoDevolve400() throws Exception {
        String corpo = PLANO_VALIDO.replace("2026-10-19T08:00:00", "ontem");
        mvc.perform(post("/planos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        org.hamcrest.Matchers.containsString("ilegível")));
    }

    @Test
    void campoObrigatorioAusenteDevolve400ComAListaDeProblemas() throws Exception {
        mvc.perform(post("/planos").contentType(MediaType.APPLICATION_JSON).content("{\"pilotoId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problemas").isArray())
                .andExpect(jsonPath("$.problemas.length()").value(org.hamcrest.Matchers.greaterThan(1)));
    }

    @Test
    void situacaoInexistenteDevolve400ListandoAsValidas() throws Exception {
        mvc.perform(get("/planos").param("situacao", "VOANDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problemas[0]").value(
                        org.hamcrest.Matchers.containsString("EM_EDICAO")));
    }

    @Test
    void idNaoNumericoDevolve400ENao422() throws Exception {
        mvc.perform(get("/planos/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problemas[0]").value(
                        org.hamcrest.Matchers.containsString("Long")));
    }

    @Test
    void parametroObrigatorioAusenteDevolve400Nomeando() throws Exception {
        mvc.perform(get("/aerovias/R1/altitudes-disponiveis").param("inicio", "2026-10-19T08:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.problemas[0]").value(
                        org.hamcrest.Matchers.containsString("fim")));
    }

    @Test
    void planoInexistenteDevolve404() throws Exception {
        mvc.perform(get("/planos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("não encontrado")));
    }

    @Test
    void aeroviaInexistenteNaDisponibilidadeDevolve404() throws Exception {
        mvc.perform(get("/aerovias/R99/altitudes-disponiveis")
                        .param("inicio", "2026-10-19T08:00:00").param("fim", "2026-10-19T09:00:00"))
                .andExpect(status().isNotFound());
    }

    @Test
    void periodoInvertidoDevolve422() throws Exception {
        mvc.perform(get("/aerovias/R1/altitudes-disponiveis")
                        .param("inicio", "2026-10-19T10:00:00").param("fim", "2026-10-19T08:00:00"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void aeroviaDuplicadaDevolve409() throws Exception {
        mvc.perform(post("/aerovias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"R1\",\"origem\":\"POA\",\"destino\":\"FLN\",\"distanciaKm\":363}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("não pode ser alterada")));
    }

    @Test
    void categoriaDeAeronaveDesconhecidaDevolve422() throws Exception {
        mvc.perform(post("/aeronaves").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"prefixo":"PR-ZZZ","categoria":"FOGUETE","velocidadeCruzeiroKmh":900,
                                 "autonomiaKm":3000}"""))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("FOGUETE")));
    }

    @Test
    void modificarPlanoForaDeEdicaoDevolve409() throws Exception {
        mvc.perform(post("/planos/1005/trechos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"aeroviaId":"R1","altitude":30000,"horarioEntrada":"2026-10-19T08:00:00"}"""))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("EM_EDICAO")));
    }

    @Test
    void rotaInexistenteDevolve404ENao500() throws Exception {
        mvc.perform(get("/esta-rota-nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("Rota não encontrada")));
    }

    @Test
    void metodoNaoSuportadoDevolve405ENao500() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/aeroportos"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void contentTypeAusenteDevolve415ENao500() throws Exception {
        mvc.perform(post("/planos").content(PLANO_VALIDO))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415));
    }
}
