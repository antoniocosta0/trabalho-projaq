package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_vaga")
public class ReservaVagaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plano_id", nullable = false)
    private Long planoId;

    @Column(name = "aeroporto_codigo", nullable = false)
    private String aeroportoCodigo;

    @Column(name = "numero_vaga", nullable = false)
    private int numeroVaga;

    @Column(name = "slot_inicio", nullable = false)
    private LocalDateTime slotInicio;

    protected ReservaVagaEntity() { }

    public ReservaVagaEntity(Long planoId, String aeroportoCodigo, int numeroVaga, LocalDateTime slotInicio) {
        this.planoId = planoId;
        this.aeroportoCodigo = aeroportoCodigo;
        this.numeroVaga = numeroVaga;
        this.slotInicio = slotInicio;
    }

    public Long getPlanoId() { return planoId; }
    public String getAeroportoCodigo() { return aeroportoCodigo; }
    public int getNumeroVaga() { return numeroVaga; }
    public LocalDateTime getSlotInicio() { return slotInicio; }
}
