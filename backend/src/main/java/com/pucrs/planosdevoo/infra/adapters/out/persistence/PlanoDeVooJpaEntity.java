package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import com.pucrs.planosdevoo.domain.model.SituacaoPlano;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "plano_de_voo")
public class PlanoDeVooJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "piloto_id")
    private PilotoJpaEntity piloto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aeronave_prefixo")
    private AeronaveJpaEntity aeronave;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origem_codigo")
    private AeroportoJpaEntity origem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destino_codigo")
    private AeroportoJpaEntity destino;

    @Column(nullable = false)
    private LocalDateTime partida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoPlano situacao;

    protected PlanoDeVooJpaEntity() { }

    public PlanoDeVooJpaEntity(Long id, PilotoJpaEntity piloto, AeronaveJpaEntity aeronave, AeroportoJpaEntity origem,
                            AeroportoJpaEntity destino, LocalDateTime partida, SituacaoPlano situacao) {
        this.id = id;
        this.piloto = piloto;
        this.aeronave = aeronave;
        this.origem = origem;
        this.destino = destino;
        this.partida = partida;
        this.situacao = situacao;
    }

    public Long getId() { return id; }
    public PilotoJpaEntity getPiloto() { return piloto; }
    public AeronaveJpaEntity getAeronave() { return aeronave; }
    public AeroportoJpaEntity getOrigem() { return origem; }
    public AeroportoJpaEntity getDestino() { return destino; }
    public LocalDateTime getPartida() { return partida; }
    public SituacaoPlano getSituacao() { return situacao; }
}
