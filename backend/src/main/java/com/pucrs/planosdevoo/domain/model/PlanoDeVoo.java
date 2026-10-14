package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;

public class PlanoDeVoo {

    private final Long id;
    private final Piloto piloto;
    private final Aeronave aeronave;
    private final Aeroporto origem;
    private final Aeroporto destino;
    private final LocalDateTime partida;
    private final SituacaoPlano situacao;

    private PlanoDeVoo(Long id, Piloto piloto, Aeronave aeronave, Aeroporto origem,
                       Aeroporto destino, LocalDateTime partida, SituacaoPlano situacao) {
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
    }

    // Todo plano novo nasce em edição e ainda sem identificador.
    public static PlanoDeVoo novo(Piloto piloto, Aeronave aeronave, Aeroporto origem,
                                  Aeroporto destino, LocalDateTime partida) {
        return new PlanoDeVoo(null, piloto, aeronave, origem, destino, partida, SituacaoPlano.EM_EDICAO);
    }

    // Usado ao ler um plano já salvo.
    public static PlanoDeVoo reconstituir(Long id, Piloto piloto, Aeronave aeronave, Aeroporto origem,
                                          Aeroporto destino, LocalDateTime partida, SituacaoPlano situacao) {
        return new PlanoDeVoo(id, piloto, aeronave, origem, destino, partida, situacao);
    }

    public Long getId() { return id; }
    public Piloto getPiloto() { return piloto; }
    public Aeronave getAeronave() { return aeronave; }
    public Aeroporto getOrigem() { return origem; }
    public Aeroporto getDestino() { return destino; }
    public LocalDateTime getPartida() { return partida; }
    public SituacaoPlano getSituacao() { return situacao; }
}
