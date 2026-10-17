package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Raiz do agregado: o percurso (trechos) só muda por aqui, e só em edição.
public class PlanoDeVoo {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final Long id;
    private final Piloto piloto;
    private final Aeronave aeronave;
    private final Aeroporto origem;
    private final Aeroporto destino;
    private final LocalDateTime partida;
    private SituacaoPlano situacao;
    private final List<Trecho> trechos;

    private PlanoDeVoo(Long id, Piloto piloto, Aeronave aeronave, Aeroporto origem, Aeroporto destino,
                       LocalDateTime partida, SituacaoPlano situacao, List<Trecho> trechos) {
        if (piloto == null || aeronave == null || origem == null || destino == null || partida == null) {
            throw new IllegalArgumentException("Piloto, aeronave, origem, destino e partida são obrigatórios");
        }
        this.id = id;
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.partida = partida;
        this.situacao = situacao;
        this.trechos = new ArrayList<>(trechos);
    }

    // Todo plano novo nasce em edição, sem identificador e sem percurso.
    public static PlanoDeVoo novo(Piloto piloto, Aeronave aeronave, Aeroporto origem,
                                  Aeroporto destino, LocalDateTime partida) {
        return new PlanoDeVoo(null, piloto, aeronave, origem, destino, partida,
                SituacaoPlano.EM_EDICAO, List.of());
    }

    // Usado ao ler um plano já salvo.
    public static PlanoDeVoo reconstituir(Long id, Piloto piloto, Aeronave aeronave, Aeroporto origem,
                                          Aeroporto destino, LocalDateTime partida, SituacaoPlano situacao,
                                          List<Trecho> trechos) {
        return new PlanoDeVoo(id, piloto, aeronave, origem, destino, partida, situacao, trechos);
    }

    // ---------- percurso (UC06) ----------

    // O trecho entra no fim do percurso. A velocidade vem sempre da aeronave
    // do plano, para o horário de chegada nunca ficar inconsistente.
    public Trecho adicionarTrecho(Aerovia aerovia, int altitude, LocalDateTime entrada, Pouso pouso) {
        garantirEmEdicao("alterar o percurso");
        Trecho trecho = criarTrecho(trechos.size() + 1, aerovia, altitude, entrada, pouso);
        trechos.add(trecho);
        return trecho;
    }

    public Trecho alterarTrecho(int ordem, Aerovia aerovia, int altitude, LocalDateTime entrada, Pouso pouso) {
        garantirEmEdicao("alterar o percurso");
        int indice = indiceDo(ordem);
        Trecho novo = criarTrecho(ordem, aerovia, altitude, entrada, pouso);
        trechos.set(indice, novo);
        return novo;
    }

    // Remove o trecho e renumera os seguintes, para a ordem continuar 1, 2, 3...
    public void removerTrecho(int ordem) {
        garantirEmEdicao("alterar o percurso");
        trechos.remove(indiceDo(ordem));
        for (int i = 0; i < trechos.size(); i++) {
            Trecho t = trechos.get(i);
            if (t.getOrdem() != i + 1) {
                trechos.set(i, criarTrecho(i + 1, t.getAerovia(), t.getAltitude(), t.getHorarioEntrada(), t.getPouso()));
            }
        }
    }

    // ---------- abandono (UC11) ----------

    public void abandonar() {
        garantirEmEdicao("abandonar o plano");
        situacao = SituacaoPlano.ABANDONADO;
    }

    // ---------- validação (UC09) ----------

    // Confere tudo o que depende só do próprio plano e devolve a lista de
    // problemas (vazia se estiver tudo certo). Não muda a situação e não
    // reserva nada. A disponibilidade de altitudes e vagas depende das reservas
    // de OUTROS planos, então fica no caso de uso ValidarPlanoUC, não aqui.
    public List<String> validar() {
        garantirEmEdicao("validar o plano");
        List<String> problemas = new ArrayList<>();
        if (trechos.isEmpty()) {
            problemas.add("O percurso não tem nenhum trecho");
            return problemas;
        }
        verificarPercurso(problemas);
        verificarHorarios(problemas);
        verificarMesmoDia(problemas);
        verificarAeronave(problemas);
        verificarAutonomia(problemas);
        return problemas;
    }

    // 1. As aerovias se encaixam, e o voo termina com pouso no destino.
    private void verificarPercurso(List<String> problemas) {
        Trecho primeiro = trechos.get(0);
        if (!mesmoAeroporto(primeiro.getAerovia().getOrigem(), origem)) {
            problemas.add("Trecho 1 sai de " + primeiro.getAerovia().getOrigem().getCodigo()
                    + ", mas o plano parte de " + origem.getCodigo());
        }
        for (int i = 1; i < trechos.size(); i++) {
            Aeroporto chegouEm = trechos.get(i - 1).getAerovia().getDestino();
            Aeroporto saiDe = trechos.get(i).getAerovia().getOrigem();
            if (!mesmoAeroporto(chegouEm, saiDe)) {
                problemas.add("Trecho " + (i + 1) + " sai de " + saiDe.getCodigo()
                        + ", mas o trecho " + i + " termina em " + chegouEm.getCodigo());
            }
        }
        Trecho ultimo = ultimoTrecho();
        if (!mesmoAeroporto(ultimo.getAerovia().getDestino(), destino)) {
            problemas.add("O último trecho termina em " + ultimo.getAerovia().getDestino().getCodigo()
                    + ", mas o destino do plano é " + destino.getCodigo());
        }
        if (ultimo.getPouso() == null) {
            problemas.add("O voo precisa terminar com pouso no destino final");
        }
        for (Trecho t : trechos) {
            Aeroporto aeroporto = t.getAerovia().getDestino();
            if (t.getPouso() != null && !aeroporto.possuiVaga(t.getPouso().getNumeroVaga())) {
                problemas.add("Trecho " + t.getOrdem() + ": a vaga " + t.getPouso().getNumeroVaga()
                        + " não existe em " + aeroporto.getCodigo() + " (vagas de 1 a "
                        + aeroporto.getQuantidadeVagas() + ")");
            }
        }
    }

    // 2. Cada entrada bate com a chegada calculada do trecho anterior
    //    (mais a permanência, se houve pouso). A primeira bate com a partida.
    private void verificarHorarios(List<String> problemas) {
        LocalDateTime esperado = partida;
        for (Trecho t : trechos) {
            if (!t.getHorarioEntrada().equals(esperado)) {
                problemas.add("Trecho " + t.getOrdem() + ": entrada às " + HORA.format(t.getHorarioEntrada())
                        + ", mas deveria ser às " + HORA.format(esperado));
            }
            esperado = t.getPouso() == null ? t.horarioChegada() : t.getPouso().saida(t.horarioChegada());
        }
    }

    // 3. A chegada ao destino final acontece no mesmo dia da partida.
    private void verificarMesmoDia(List<String> problemas) {
        LocalDateTime chegadaFinal = ultimoTrecho().horarioChegada();
        if (!chegadaFinal.toLocalDate().equals(partida.toLocalDate())) {
            problemas.add("O voo chega ao destino em " + HORA.format(chegadaFinal)
                    + ", fora do dia da partida");
        }
    }

    // 4. A categoria da aeronave permite voar naquela altitude e horário.
    //    Cada categoria decide sozinha (polimorfismo), sem instanceof aqui.
    private void verificarAeronave(List<String> problemas) {
        for (Trecho t : trechos) {
            if (!aeronave.podeOperar(t.getAltitude(), t.getHorarioEntrada(), t.horarioChegada())) {
                problemas.add("Trecho " + t.getOrdem() + ": a aeronave " + aeronave.getPrefixo() + " ("
                        + aeronave.getCategoria() + ") não pode voar a " + t.getAltitude() + " pés entre "
                        + HORA.format(t.getHorarioEntrada()) + " e " + HORA.format(t.horarioChegada()));
            }
        }
    }

    // 5. Desde o último abastecimento, no máximo 85% da autonomia.
    //    Sobrevoo continua somando; pouso abastece e zera a conta.
    private void verificarAutonomia(List<String> problemas) {
        double limite = aeronave.distanciaMaxSemAbastecer();
        double acumulado = 0;
        for (Trecho t : trechos) {
            acumulado += t.getAerovia().getDistanciaKm();
            if (acumulado > limite) {
                problemas.add("Trecho " + t.getOrdem() + ": " + Math.round(acumulado)
                        + " km sem abastecer, acima do limite de " + Math.round(limite)
                        + " km (85% da autonomia de " + Math.round(aeronave.getAutonomiaKm()) + " km)");
            }
            if (t.getPouso() != null) {
                acumulado = 0;
            }
        }
    }

    // ---------- auxiliares ----------

    private void garantirEmEdicao(String acao) {
        if (situacao != SituacaoPlano.EM_EDICAO) {
            throw new OperacaoNaoPermitidaException("Não é possível " + acao + ": o plano está " + situacao
                    + ", e só planos EM_EDICAO podem ser modificados");
        }
    }

    private Trecho criarTrecho(int ordem, Aerovia aerovia, int altitude, LocalDateTime entrada, Pouso pouso) {
        return new Trecho(ordem, aerovia, altitude, entrada, aeronave.getVelocidadeCruzeiroKmh(), pouso);
    }

    private int indiceDo(int ordem) {
        if (ordem < 1 || ordem > trechos.size()) {
            throw new RecursoNaoEncontradoException("O plano não tem trecho de ordem " + ordem);
        }
        return ordem - 1;
    }

    private Trecho ultimoTrecho() {
        return trechos.get(trechos.size() - 1);
    }

    private static boolean mesmoAeroporto(Aeroporto a, Aeroporto b) {
        return a.getCodigo().equals(b.getCodigo());
    }

    public Long getId() { return id; }
    public Piloto getPiloto() { return piloto; }
    public Aeronave getAeronave() { return aeronave; }
    public Aeroporto getOrigem() { return origem; }
    public Aeroporto getDestino() { return destino; }
    public LocalDateTime getPartida() { return partida; }
    public SituacaoPlano getSituacao() { return situacao; }
    public List<Trecho> getTrechos() { return List.copyOf(trechos); }
}
