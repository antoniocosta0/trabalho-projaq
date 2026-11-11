package com.pucrs.planosdevoo.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_evento")
public class OutboxEventoEntity {

    @Id
    @Column(name = "evento_id")
    private String eventoId;

    @Column(name = "plano_id", nullable = false)
    private Long planoId;

    @Column(name = "aprovado_em", nullable = false)
    private LocalDateTime aprovadoEm;

    @Column(name = "publicado_em")
    private LocalDateTime publicadoEm;

    protected OutboxEventoEntity() { }

    public OutboxEventoEntity(String eventoId, Long planoId, LocalDateTime aprovadoEm) {
        this.eventoId = eventoId;
        this.planoId = planoId;
        this.aprovadoEm = aprovadoEm;
    }

    public String getEventoId() { return eventoId; }
    public Long getPlanoId() { return planoId; }
    public LocalDateTime getAprovadoEm() { return aprovadoEm; }
    public LocalDateTime getPublicadoEm() { return publicadoEm; }
}
