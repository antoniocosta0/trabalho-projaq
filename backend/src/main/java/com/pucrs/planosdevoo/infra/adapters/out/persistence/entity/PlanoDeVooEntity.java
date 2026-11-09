package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plano_de_voo")
public class PlanoDeVooEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "piloto_id")
    private PilotoEntity piloto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aeronave_prefixo")
    private AeronaveEntity aeronave;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origem_codigo")
    private AeroportoEntity origem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destino_codigo")
    private AeroportoEntity destino;

    @Column(nullable = false)
    private LocalDateTime partida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoPlano situacao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trecho", joinColumns = @JoinColumn(name = "plano_id"))
    @OrderBy("ordem")
    private List<TrechoEmbeddable> trechos = new ArrayList<>();

    protected PlanoDeVooEntity() { }

    public PlanoDeVooEntity(Long id, PilotoEntity piloto, AeronaveEntity aeronave, AeroportoEntity origem,
                            AeroportoEntity destino, LocalDateTime partida, SituacaoPlano situacao,
                            List<TrechoEmbeddable> trechos) {
        this.id = id;
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.partida = partida;
        this.situacao = situacao;
        this.trechos = new ArrayList<>(trechos);
    }

    public Long getId() { return id; }
    public PilotoEntity getPiloto() { return piloto; }
    public AeronaveEntity getAeronave() { return aeronave; }
    public AeroportoEntity getOrigem() { return origem; }
    public AeroportoEntity getDestino() { return destino; }
    public LocalDateTime getPartida() { return partida; }
    public SituacaoPlano getSituacao() { return situacao; }
    public List<TrechoEmbeddable> getTrechos() { return trechos; }
}
