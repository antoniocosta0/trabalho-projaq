package com.pucrs.planosdevoo.domain.model;

import com.pucrs.planosdevoo.domain.exceptions.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.exceptions.PlanoInvalidoException;
import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlanoDeVoo {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final Long id;
    private Piloto piloto;
    private Aeronave aeronave;
    private String origem;
    private String destino;
    private LocalDateTime partida;
    private SituacaoPlano situacao;
    private final List<Trecho> trechos;

    private PlanoDeVoo(Long id, Piloto piloto, Aeronave aeronave, String origem, String destino,
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

    public static PlanoDeVoo novo(Piloto piloto, Aeronave aeronave, String origem,
                                  String destino, LocalDateTime partida) {
        return new PlanoDeVoo(null, piloto, aeronave, origem, destino, partida,
                SituacaoPlano.EM_EDICAO, List.of());
    }

    public static PlanoDeVoo reconstituir(Long id, Piloto piloto, Aeronave aeronave, String origem,
                                          String destino, LocalDateTime partida, SituacaoPlano situacao,
                                          List<Trecho> trechos) {
        return new PlanoDeVoo(id, piloto, aeronave, origem, destino, partida, situacao, trechos);
    }

    public void alterarDados(Piloto piloto, Aeronave aeronave, String origem, String destino,
                             LocalDateTime partida) {
        garantirEmEdicao("alterar o plano");
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.partida = partida;
        trechos.replaceAll(t -> criarTrecho(t.getOrdem(), t.getAerovia(), t.getAltitude(), t.getHorarioEntrada(),
                t.getPouso()));
    }

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

    public void abandonar() {
        garantirEmEdicao("abandonar o plano");
        situacao = SituacaoPlano.ABANDONADO;
    }

    public void aprovar(List<String> problemasDosRecursos) {
        garantirEmEdicao("aprovar o plano");
        List<String> problemas = new ArrayList<>(validar());
        problemas.addAll(problemasDosRecursos);
        if (!problemas.isEmpty()) {
            throw new PlanoInvalidoException(problemas);
        }
        situacao = SituacaoPlano.APROVADO;
    }

    public void cancelar(LocalDateTime agora) {
        if (situacao != SituacaoPlano.APROVADO) {
            throw new OperacaoNaoPermitidaException("Não é possível cancelar o plano: ele está " + situacao
                    + ", e só planos APROVADO podem ser cancelados");
        }
        if (!partida.isAfter(agora)) {
            throw new OperacaoNaoPermitidaException("Não é possível cancelar o plano: a partida (" + HORA.format(partida)
                    + ") já ocorreu");
        }
        situacao = SituacaoPlano.CANCELADO;
    }

    public List<ReservaAerovia> reservasDeAerovia() {
        return trechos.stream().flatMap(t -> t.reservasDeAerovia(id).stream()).toList();
    }

    public List<ReservaVaga> reservasDeVaga() {
        return trechos.stream().flatMap(t -> t.reservasDeVaga(id).stream()).toList();
    }

    public List<String> validar() {
        garantirEmEdicao("validar o plano");
        List<String> problemas = new ArrayList<>();
        if (trechos.isEmpty()) {
            problemas.add("O percurso não tem nenhum trecho");
            return problemas;
        }
        verificarPercurso(problemas);
        verificarPousoNoDestino(problemas);
        verificarHorarios(problemas);
        verificarMesmoDia(problemas);
        verificarAeronave(problemas);
        verificarAutonomia(problemas);
        return problemas;
    }

    private void verificarPercurso(List<String> problemas) {
        Trecho primeiro = trechos.get(0);
        if (!primeiro.getAerovia().getOrigem().equals(origem)) {
            problemas.add("Trecho 1 sai de " + primeiro.getAerovia().getOrigem() + ", mas o plano parte de " + origem);
        }
        for (int i = 1; i < trechos.size(); i++) {
            String chegouEm = trechos.get(i - 1).getAerovia().getDestino();
            String saiDe = trechos.get(i).getAerovia().getOrigem();
            if (!chegouEm.equals(saiDe)) {
                problemas.add("Trecho " + (i + 1) + " sai de " + saiDe + ", mas o trecho " + i + " termina em " + chegouEm);
            }
        }
        Trecho ultimo = ultimoTrecho();
        if (!ultimo.getAerovia().getDestino().equals(destino)) {
            problemas.add("O último trecho termina em " + ultimo.getAerovia().getDestino()
                    + ", mas o destino do plano é " + destino);
        }
    }

    private void verificarPousoNoDestino(List<String> problemas) {
        if (ultimoTrecho().getPouso() == null) {
            problemas.add("O voo precisa terminar com pouso no destino final");
        }
    }

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

    private void verificarMesmoDia(List<String> problemas) {
        LocalDateTime chegadaFinal = ultimoTrecho().horarioChegada();
        if (!chegadaFinal.toLocalDate().equals(partida.toLocalDate())) {
            problemas.add("O voo chega ao destino em " + HORA.format(chegadaFinal)
                    + ", fora do dia da partida");
        }
    }

    private void verificarAeronave(List<String> problemas) {
        for (Trecho t : trechos) {
            if (!aeronave.podeOperar(t.getAltitude(), t.getHorarioEntrada(), t.horarioChegada())) {
                problemas.add("Trecho " + t.getOrdem() + ": a aeronave " + aeronave.getPrefixo() + " ("
                        + aeronave.getCategoria() + ") não pode voar a " + t.getAltitude() + " pés entre "
                        + HORA.format(t.getHorarioEntrada()) + " e " + HORA.format(t.horarioChegada()));
            }
        }
    }

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

    public Long getId() { return id; }
    public Piloto getPiloto() { return piloto; }
    public Aeronave getAeronave() { return aeronave; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public LocalDateTime getPartida() { return partida; }
    public SituacaoPlano getSituacao() { return situacao; }
    public List<Trecho> getTrechos() { return List.copyOf(trechos); }
}
