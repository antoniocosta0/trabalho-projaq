package com.pucrs.planosdevoo.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

// As bordas onde o enunciado não é literal e o código precisou interpretar.
// Cada uma delas está registrada na ADR 0010; estes testes são o que impede a
// interpretação de mudar sem querer.
class BordasDoEnunciadoTest {

    private static final Aeroporto POA = new Aeroporto("POA", "Salgado Filho", 41);
    private static final Aeroporto FLN = new Aeroporto("FLN", "Hercílio Luz", 16);
    private static final Aerovia R1 = new Aerovia("R1", POA, FLN, 363);
    private static final Piloto PILOTO = new Piloto(1L, "Ana Souza");

    private static LocalDateTime em(int dia, int hora, int minuto) {
        return LocalDateTime.of(2026, 10, dia, hora, minuto);
    }

    // ---------- borda 2 da ADR 0010: a janela da aeronave de carga ----------

    @Test
    void cargaQueTerminaExatamenteAs0600ERecusada() {
        AeronaveCarga carga = new AeronaveCarga("PR-CRG", 780, 4500, "Sul Cargas", 20);

        assertFalse(carga.podeOperar(30_000, em(19, 5, 0), em(19, 6, 0)));
    }

    @Test
    void cargaQuePassaUmMinutoDas0600ERecusada() {
        AeronaveCarga carga = new AeronaveCarga("PR-CRG", 780, 4500, "Sul Cargas", 20);

        assertFalse(carga.podeOperar(30_000, em(19, 5, 0), em(19, 6, 1)));
    }

    @Test
    void cargaQueComecaExatamenteA0000EAceita() {
        AeronaveCarga carga = new AeronaveCarga("PR-CRG", 780, 4500, "Sul Cargas", 20);

        assertTrue(carga.podeOperar(30_000, em(19, 0, 0), em(19, 1, 0)));
    }

    // ---------- borda 3 da ADR 0010: arredondamento da duração ----------

    @Test
    void duracaoEArredondadaAoMinuto() {
        // 363 km a 830 km/h = 26 min e 14 s. Sem arredondar, nenhum horário
        // digitado em minutos bateria com a chegada calculada.
        assertEquals(26, R1.duracao(830).toMinutes());
        assertEquals(0, R1.duracao(830).toSecondsPart());
    }

    @Test
    void chegadaCalculadaUsaADuracaoArredondada() {
        Trecho t = new Trecho(1, R1, 30_000, em(19, 8, 0), 830, null);

        assertEquals(em(19, 8, 26), t.horarioChegada());
    }

    // ---------- limite diário: o voo termina no dia, a permanência não ----------

    @Test
    void chegadaAs2359NoMesmoDiaEValidaMesmoComPermanenciaCruzandoAMeiaNoite() {
        // Partida 23:33, chegada 23:59 do mesmo dia: o voo cabe no dia. A
        // permanência de 120 min vai até 01:59 do dia seguinte, e isso é
        // permitido — o enunciado limita a CHEGADA, não a permanência.
        PlanoDeVoo p = PlanoDeVoo.reconstituir(1L, PILOTO, passageiros(), POA, FLN, em(19, 23, 33),
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, em(19, 23, 33), new Pouso(3, 120));

        assertEquals(List.of(), p.validar());
        assertEquals(em(19, 23, 59), p.getTrechos().get(0).horarioChegada());
        assertEquals(em(20, 1, 59), p.getTrechos().get(0).getPouso().saida(em(19, 23, 59)));
    }

    @Test
    void chegadaQueViraODiaEInvalida() {
        PlanoDeVoo p = PlanoDeVoo.reconstituir(1L, PILOTO, passageiros(), POA, FLN, em(19, 23, 40),
                SituacaoPlano.EM_EDICAO, List.of());
        p.adicionarTrecho(R1, 30_000, em(19, 23, 40), new Pouso(3, 30));

        assertTrue(p.validar().stream().anyMatch(m -> m.contains("fora do dia da partida")));
    }

    // ---------- faixa de altitudes ----------

    @Test
    void altitudeAbaixoDe25000ERecusada() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trecho(1, R1, 24_000, em(19, 8, 0), 830, null));
    }

    @Test
    void altitudeAcimaDe35000ERecusada() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trecho(1, R1, 36_000, em(19, 8, 0), 830, null));
    }

    @Test
    void altitudeForaDaEscalaDeMilERecusada() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trecho(1, R1, 30_500, em(19, 8, 0), 830, null));
    }

    @Test
    void osDoisExtremosDaFaixaSaoAceitos() {
        assertEquals(25_000, new Trecho(1, R1, 25_000, em(19, 8, 0), 830, null).getAltitude());
        assertEquals(35_000, new Trecho(1, R1, 35_000, em(19, 8, 0), 830, null).getAltitude());
    }

    // ---------- vaga e permanência ----------

    @Test
    void vagaZeroOuNegativaERecusada() {
        assertThrows(IllegalArgumentException.class, () -> new Pouso(0, 30));
        assertThrows(IllegalArgumentException.class, () -> new Pouso(-1, 30));
    }

    @Test
    void permanenciaZeroERecusada() {
        assertThrows(IllegalArgumentException.class, () -> new Pouso(3, 0));
    }

    @Test
    void vagaAcimaDaQuantidadeDoAeroportoNaoExiste() {
        assertTrue(FLN.possuiVaga(16));
        assertFalse(FLN.possuiVaga(17));
        assertFalse(FLN.possuiVaga(0));
    }

    @Test
    void aeroportoSemVagaNaoAceitaPouso() {
        // Aeroporto com zero vagas é permitido no cadastro — é um aeroporto que
        // só pode ser sobrevoado. Nenhum número de vaga existe nele.
        Aeroporto semVagas = new Aeroporto("XXX", "Pista sem pátio", 0);

        assertFalse(semVagas.possuiVaga(1));
    }

    // ---------- aerovia liga dois aeroportos diferentes ----------

    @Test
    void aeroviaComOrigemIgualAoDestinoERecusada() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new Aerovia("R0", POA, POA, 10));

        assertTrue(e.getMessage().contains("dois aeroportos diferentes"));
    }

    private static Aeronave passageiros() {
        return new AeronavePassageiros("PR-PAX", 830, 5000, "Linhas do Sul", 180);
    }
}
