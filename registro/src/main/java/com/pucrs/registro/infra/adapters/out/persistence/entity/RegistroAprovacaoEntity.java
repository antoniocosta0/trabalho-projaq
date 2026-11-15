package com.pucrs.registro.infra.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_aprovacao")
public class RegistroAprovacaoEntity {

    @Id
    @Column(name = "evento_id")
    private String eventoId;

    @Column(name = "plano_id", nullable = false, unique = true)
    private Long planoId;

    @Column(name = "aprovado_em")
    private LocalDateTime aprovadoEm;

    @Column(name = "recebido_em", nullable = false)
    private LocalDateTime recebidoEm;

    @Lob
    @Column(nullable = false)
    private String conteudo;

    protected RegistroAprovacaoEntity() { }

    public RegistroAprovacaoEntity(String eventoId, Long planoId, LocalDateTime aprovadoEm, LocalDateTime recebidoEm,
                                   String conteudo) {
        this.eventoId = eventoId;
        this.planoId = planoId;
        this.aprovadoEm = aprovadoEm;
        this.recebidoEm = recebidoEm;
        this.conteudo = conteudo;
    }

    public String getEventoId() { return eventoId; }
    public Long getPlanoId() { return planoId; }
    public LocalDateTime getAprovadoEm() { return aprovadoEm; }
    public LocalDateTime getRecebidoEm() { return recebidoEm; }
    public String getConteudo() { return conteudo; }
}
