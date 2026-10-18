package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_aerovia")
public class ReservaAeroviaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plano_id", nullable = false)
    private Long planoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aerovia_id")
    private AeroviaJpaEntity aerovia;

    @Column(nullable = false)
    private int altitude;

    @Column(name = "slot_inicio", nullable = false)
    private LocalDateTime slotInicio;

    protected ReservaAeroviaJpaEntity() { }

    public ReservaAeroviaJpaEntity(Long planoId, AeroviaJpaEntity aerovia, int altitude, LocalDateTime slotInicio) {
        this.planoId = planoId;
        this.aerovia = aerovia;
        this.altitude = altitude;
        this.slotInicio = slotInicio;
    }

    public Long getPlanoId() { return planoId; }
    public AeroviaJpaEntity getAerovia() { return aerovia; }
    public int getAltitude() { return altitude; }
    public LocalDateTime getSlotInicio() { return slotInicio; }
}
