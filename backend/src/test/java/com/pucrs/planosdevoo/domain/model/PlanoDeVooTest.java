package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Testes de regra pura: nenhum sobe o Spring nem toca no banco.
class PlanoDeVooTest {

    // ---------- dados de apoio (mesma malha do enunciado) ----------

    private static final Aeroporto POA = new Aeroporto("POA", "Porto Alegre", 41);
    private static final Aeroporto FLN = new Aeroporto("FLN", "Florianópolis", 16);
    private static final Aeroporto CWB = new Aeroporto("CWB", "Curitiba", 26);
    private static final Aeroporto GRU = new Aeroporto("GRU", "Guarulhos", 115);

    private static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);
    private static final Aerovia R2 = new Aerovia("R2", POA, GRU, 865);
    private static final Aerovia R4 = new Aerovia("R4", FLN, CWB, 245);

    private static final Piloto PILOTO = new Piloto(1L, "Ana Souza");

    // 830 km/h: R1 leva 26 min (363/830*60 = 26,2) e R4 leva 18 min (17,7).
    private static final Aeronave PASSAGEIROS = new AeronavePassageiros("PR-PAX", 830, 5000, "Sul", 180);
    // 500 km/h e autonomia 1000 km: limite de 850 km, menos que os 865 km da R2.
    private static final Aeronave PARTICULAR_CURTO = new AeronaveParticular("PP-CUR", 500, 1000, "Hangar");
    // 780 km/h: R1 leva 28 min.
    private static final Aeronave CARGA = new AeronaveCarga("PR-CRG", 780, 4500, "Sul Cargas", 20);

    private static LocalDateTime em(int hora, int minuto) {
        return LocalDateTime.of(2026, 10, 19, hora, minuto);
    }

    private static PlanoDeVoo plano(Aeronave aeronave, Aeroporto destino, LocalDateTime partida) {
        return PlanoDeVoo.novo(PILOTO, aeronave, POA, destino, partida);
    }

    // POA -> FLN (sobrevoo) -> CWB (pouso), partindo às 10:00. Tudo certo.
    private static PlanoDeVoo planoValido() {
        PlanoDeVoo p = plano(PASSAGEIROS, CWB, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), null);
        p.adicionarTrecho(R4, 30_000, em(10, 26), new Pouso(1, 30));
        return p;
    }

    private static void assertTemProblemaCom(List<String> problemas, String trecho) {
        assertTrue(problemas.stream().anyMatch(p -> p.contains(trecho)),
                "esperava um problema contendo \"" + trecho + "\", mas veio: " + problemas);
    }

    // ---------- percurso (UC06) ----------

    @Test
    void trechosRecebemOrdemSequencialEVelocidadeDaAeronave() {
        PlanoDeVoo p = planoValido();

        assertEquals(2, p.getTrechos().size());
        assertEquals(1, p.getTrechos().get(0).getOrdem());
        assertEquals(2, p.getTrechos().get(1).getOrdem());
        assertEquals(em(10, 26), p.getTrechos().get(0).horarioChegada());
    }

    @Test
    void removerTrechoRenumeraOsSeguintes() {
        PlanoDeVoo p = planoValido();
        p.adicionarTrecho(R1, 30_000, em(12, 0), null);

        p.removerTrecho(1);

        assertEquals(2, p.getTrechos().size());
        assertEquals("R4", p.getTrechos().get(0).getAerovia().getId());
        assertEquals(1, p.getTrechos().get(0).getOrdem());
        assertEquals(2, p.getTrechos().get(1).getOrdem());
    }

    @Test
    void alterarTrechoTrocaSoAquelaOrdem() {
        PlanoDeVoo p = planoValido();

        p.alterarTrecho(1, R1, 31_000, em(10, 0), null);

        assertEquals(31_000, p.getTrechos().get(0).getAltitude());
        assertEquals("R4", p.getTrechos().get(1).getAerovia().getId());
    }

    @Test
    void ordemInexistenteNaoEncontrada() {
        PlanoDeVoo p = planoValido();

        assertThrows(RecursoNaoEncontradoException.class, () -> p.removerTrecho(5));
    }

    // ---------- só EM_EDICAO pode ser modificado ----------

    @Test
    void abandonarMudaSituacao() {
        PlanoDeVoo p = planoValido();

        p.abandonar();

        assertEquals(SituacaoPlano.ABANDONADO, p.getSituacao());
    }

    @Test
    void planoAbandonadoNaoPodeSerAlteradoValidadoNemAbandonadoDeNovo() {
        PlanoDeVoo p = planoValido();
        p.abandonar();

        assertThrows(OperacaoNaoPermitidaException.class, () -> p.adicionarTrecho(R1, 30_000, em(10, 0), null));
        assertThrows(OperacaoNaoPermitidaException.class, () -> p.removerTrecho(1));
        assertThrows(OperacaoNaoPermitidaException.class, p::validar);
        assertThrows(OperacaoNaoPermitidaException.class, p::abandonar);
    }

    // ---------- validação (UC09) ----------

    @Test
    void planoValidoNaoTemProblemasENaoMudaDeSituacao() {
        PlanoDeVoo p = planoValido();

        assertEquals(List.of(), p.validar());
        assertEquals(SituacaoPlano.EM_EDICAO, p.getSituacao());
    }

    @Test
    void percursoVazio() {
        PlanoDeVoo p = plano(PASSAGEIROS, CWB, em(10, 0));

        assertTemProblemaCom(p.validar(), "não tem nenhum trecho");
    }

    @Test
    void primeiroTrechoNaoSaiDaOrigemDoPlano() {
        PlanoDeVoo p = plano(PASSAGEIROS, CWB, em(10, 0));
        p.adicionarTrecho(R4, 30_000, em(10, 0), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "Trecho 1 sai de FLN");
    }

    @Test
    void aeroviasNaoSeEncaixam() {
        PlanoDeVoo p = plano(PASSAGEIROS, GRU, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), null);
        p.adicionarTrecho(R2, 30_000, em(10, 26), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "Trecho 2 sai de POA, mas o trecho 1 termina em FLN");
    }

    @Test
    void ultimoTrechoNaoChegaAoDestino() {
        PlanoDeVoo p = plano(PASSAGEIROS, CWB, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "termina em FLN, mas o destino do plano é CWB");
    }

    @Test
    void destinoFinalSemPouso() {
        PlanoDeVoo p = plano(PASSAGEIROS, FLN, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), null);

        assertTemProblemaCom(p.validar(), "terminar com pouso");
    }

    @Test
    void vagaQueNaoExisteNoAeroporto() {
        PlanoDeVoo p = plano(PASSAGEIROS, FLN, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), new Pouso(99, 30));

        assertTemProblemaCom(p.validar(), "a vaga 99 não existe em FLN");
    }

    @Test
    void primeiraEntradaDiferenteDaPartida() {
        PlanoDeVoo p = plano(PASSAGEIROS, FLN, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 5), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "deveria ser às 19/10 10:00");
    }

    @Test
    void entradaAposSobrevooDiferenteDaChegadaCalculada() {
        PlanoDeVoo p = plano(PASSAGEIROS, CWB, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), null);
        p.adicionarTrecho(R4, 30_000, em(10, 40), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "deveria ser às 19/10 10:26");
    }

    @Test
    void entradaAposPousoConsideraAPermanencia() {
        PlanoDeVoo p = plano(PASSAGEIROS, CWB, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), new Pouso(1, 30));
        p.adicionarTrecho(R4, 30_000, em(10, 26), new Pouso(1, 30));

        // chegou 10:26 + 30 min de permanência = 10:56
        assertTemProblemaCom(p.validar(), "deveria ser às 19/10 10:56");
    }

    @Test
    void chegadaEmOutroDia() {
        PlanoDeVoo p = plano(PASSAGEIROS, FLN, em(23, 50));
        p.adicionarTrecho(R1, 30_000, em(23, 50), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "fora do dia da partida");
    }

    @Test
    void particularForaDasAltitudesPermitidas() {
        PlanoDeVoo p = plano(PARTICULAR_CURTO, FLN, em(10, 0));
        p.adicionarTrecho(R1, 30_000, em(10, 0), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "não pode voar a 30000 pés");
    }

    @Test
    void cargaForaDaJanelaDaMadrugada() {
        PlanoDeVoo p = plano(CARGA, FLN, em(9, 0));
        p.adicionarTrecho(R1, 30_000, em(9, 0), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "PR-CRG (CARGA) não pode voar");
    }

    @Test
    void cargaDentroDaJanelaDaMadrugada() {
        PlanoDeVoo p = plano(CARGA, FLN, em(1, 0));
        p.adicionarTrecho(R1, 30_000, em(1, 0), new Pouso(1, 30));

        assertEquals(List.of(), p.validar());
    }

    @Test
    void autonomiaInsuficiente() {
        PlanoDeVoo p = plano(PARTICULAR_CURTO, GRU, em(8, 0));
        p.adicionarTrecho(R2, 25_000, em(8, 0), new Pouso(1, 30));

        assertTemProblemaCom(p.validar(), "865 km sem abastecer, acima do limite de 850 km");
    }

    @Test
    void sobrevooAcumulaDistanciaEPousoZera() {
        // 363 + 245 = 608 km em sobrevoo: passa do limite de uma aeronave de 700 km (595 km).
        Aeronave pequena = new AeronavePassageiros("PR-PEQ", 830, 700, "Sul", 50);
        PlanoDeVoo sobrevoo = plano(pequena, CWB, em(10, 0));
        sobrevoo.adicionarTrecho(R1, 30_000, em(10, 0), null);
        sobrevoo.adicionarTrecho(R4, 30_000, em(10, 26), new Pouso(1, 30));
        assertTemProblemaCom(sobrevoo.validar(), "608 km sem abastecer");

        // Com pouso em FLN, a conta zera: 363 e depois 245, os dois abaixo de 595.
        PlanoDeVoo comPouso = plano(pequena, CWB, em(10, 0));
        comPouso.adicionarTrecho(R1, 30_000, em(10, 0), new Pouso(1, 30));
        comPouso.adicionarTrecho(R4, 30_000, em(10, 56), new Pouso(1, 30));
        assertEquals(List.of(), comPouso.validar());
    }
}
