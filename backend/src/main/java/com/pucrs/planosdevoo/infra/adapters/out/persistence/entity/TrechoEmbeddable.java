package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.PlanoDeVooEntity;
import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeroviaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

// Uma linha da tabela "trecho". Não tem identidade própria: pertence ao plano
// (é o PlanoDeVooEntity que salva e carrega os trechos junto com ele).
// numero_vaga e permanencia_minutos nulos = sobrevoo.
@Embeddable
public class TrechoEmbeddable {

    @Column(nullable = false)
    private int ordem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aerovia_id")
    private AeroviaEntity aerovia;

    @Column(nullable = false)
    private int altitude;

    @Column(name = "horario_entrada", nullable = false)
    private LocalDateTime horarioEntrada;

    @Column(name = "numero_vaga")
    private Integer numeroVaga;

    @Column(name = "permanencia_minutos")
    private Integer permanenciaMinutos;

    protected TrechoEmbeddable() { }

    public TrechoEmbeddable(int ordem, AeroviaEntity aerovia, int altitude, LocalDateTime horarioEntrada,
                               Integer numeroVaga, Integer permanenciaMinutos) {
        this.ordem = ordem;
        this.aerovia = aerovia;
        this.altitude = altitude;
        this.horarioEntrada = horarioEntrada;
        this.numeroVaga = numeroVaga;
        this.permanenciaMinutos = permanenciaMinutos;
    }

    public int getOrdem() { return ordem; }
    public AeroviaEntity getAerovia() { return aerovia; }
    public int getAltitude() { return altitude; }
    public LocalDateTime getHorarioEntrada() { return horarioEntrada; }
    public Integer getNumeroVaga() { return numeroVaga; }
    public Integer getPermanenciaMinutos() { return permanenciaMinutos; }
}
