package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import com.pucrs.planosdevoo.infra.adapters.out.persistence.entity.AeroportoEntity;
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
@Table(name = "reserva_vaga")
public class ReservaVagaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plano_id", nullable = false)
    private Long planoId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aeroporto_codigo")
    private AeroportoEntity aeroporto;

    @Column(name = "numero_vaga", nullable = false)
    private int numeroVaga;

    @Column(name = "slot_inicio", nullable = false)
    private LocalDateTime slotInicio;

    protected ReservaVagaEntity() { }

    public ReservaVagaEntity(Long planoId, AeroportoEntity aeroporto, int numeroVaga, LocalDateTime slotInicio) {
        this.planoId = planoId;
        this.aeroporto = aeroporto;
        this.numeroVaga = numeroVaga;
        this.slotInicio = slotInicio;
    }

    public Long getPlanoId() { return planoId; }
    public AeroportoEntity getAeroporto() { return aeroporto; }
    public int getNumeroVaga() { return numeroVaga; }
    public LocalDateTime getSlotInicio() { return slotInicio; }
}
