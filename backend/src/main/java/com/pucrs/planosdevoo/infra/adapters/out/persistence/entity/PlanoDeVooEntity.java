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

    @Column(nullable = false)
    private String criador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "piloto_id")
    private PilotoEntity piloto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aeronave_prefixo")
    private AeronaveEntity aeronave;

    @Column(name = "origem_codigo", nullable = false)
    private String origem;

    @Column(name = "destino_codigo", nullable = false)
    private String destino;

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

    public PlanoDeVooEntity(Long id, String criador, PilotoEntity piloto, AeronaveEntity aeronave, String origem,
                            String destino, LocalDateTime partida, SituacaoPlano situacao,
                            List<TrechoEmbeddable> trechos) {
        this.id = id;
        this.criador = criador;
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.partida = partida;
        this.situacao = situacao;
        this.trechos = new ArrayList<>(trechos);
    }

    public Long getId() { return id; }
    public String getCriador() { return criador; }
    public PilotoEntity getPiloto() { return piloto; }
    public AeronaveEntity getAeronave() { return aeronave; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public LocalDateTime getPartida() { return partida; }
    public SituacaoPlano getSituacao() { return situacao; }
    public List<TrechoEmbeddable> getTrechos() { return trechos; }
}
