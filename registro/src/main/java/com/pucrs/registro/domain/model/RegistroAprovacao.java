package com.pucrs.registro.domain.model;

import java.time.LocalDateTime;

// cópia da aprovação como foi recebida: a consulta não depende da gestão nem do cadastro
public class RegistroAprovacao {

    private final String eventoId;
    private final Long planoId;
    private final LocalDateTime aprovadoEm;
    private final LocalDateTime recebidoEm;
    private final String conteudo;

    public RegistroAprovacao(String eventoId, Long planoId, LocalDateTime aprovadoEm, LocalDateTime recebidoEm,
                             String conteudo) {
        if (eventoId == null || planoId == null || conteudo == null) {
            throw new IllegalArgumentException("Evento, plano e conteúdo são obrigatórios");
        }
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
