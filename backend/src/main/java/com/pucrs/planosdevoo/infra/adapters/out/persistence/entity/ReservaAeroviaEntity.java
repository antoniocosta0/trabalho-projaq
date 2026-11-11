package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_aerovia")
public class ReservaAeroviaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plano_id", nullable = false)
    private Long planoId;

    @Column(name = "aerovia_id", nullable = false)
    private String aeroviaId;

    @Column(nullable = false)
    private int altitude;

    @Column(name = "slot_inicio", nullable = false)
    private LocalDateTime slotInicio;

    protected ReservaAeroviaEntity() { }

    public ReservaAeroviaEntity(Long planoId, String aeroviaId, int altitude, LocalDateTime slotInicio) {
        this.planoId = planoId;
        this.aeroviaId = aeroviaId;
        this.altitude = altitude;
        this.slotInicio = slotInicio;
    }

    public Long getPlanoId() { return planoId; }
    public String getAeroviaId() { return aeroviaId; }
    public int getAltitude() { return altitude; }
    public LocalDateTime getSlotInicio() { return slotInicio; }
}
