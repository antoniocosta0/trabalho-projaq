package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class TrechoEmbeddable {

    @Column(nullable = false)
    private int ordem;

    @Column(name = "aerovia_id", nullable = false)
    private String aeroviaId;

    @Column(name = "origem_codigo", nullable = false)
    private String origem;

    @Column(name = "destino_codigo", nullable = false)
    private String destino;

    @Column(name = "distancia_km", nullable = false)
    private double distanciaKm;

    @Column(nullable = false)
    private int altitude;

    @Column(name = "horario_entrada", nullable = false)
    private LocalDateTime horarioEntrada;

    @Column(name = "numero_vaga")
    private Integer numeroVaga;

    @Column(name = "permanencia_minutos")
    private Integer permanenciaMinutos;

    protected TrechoEmbeddable() { }

    public TrechoEmbeddable(int ordem, String aeroviaId, String origem, String destino, double distanciaKm,
                            int altitude, LocalDateTime horarioEntrada, Integer numeroVaga,
                            Integer permanenciaMinutos) {
        this.ordem = ordem;
        this.aeroviaId = aeroviaId;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
        this.altitude = altitude;
        this.horarioEntrada = horarioEntrada;
        this.numeroVaga = numeroVaga;
        this.permanenciaMinutos = permanenciaMinutos;
    }

    public int getOrdem() { return ordem; }
    public String getAeroviaId() { return aeroviaId; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
    public int getAltitude() { return altitude; }
    public LocalDateTime getHorarioEntrada() { return horarioEntrada; }
    public Integer getNumeroVaga() { return numeroVaga; }
    public Integer getPermanenciaMinutos() { return permanenciaMinutos; }
}
