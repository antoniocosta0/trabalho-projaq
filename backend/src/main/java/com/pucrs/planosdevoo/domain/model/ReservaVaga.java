package com.pucrs.planosdevoo.domain.model;

public class ReservaVaga {

    private final Long planoId;
    private final String aeroportoCodigo;
    private final int numeroVaga;
    private final Slot slot;

    public ReservaVaga(Long planoId, String aeroportoCodigo, int numeroVaga, Slot slot) {
        if (planoId == null || aeroportoCodigo == null || slot == null) {
            throw new IllegalArgumentException("Plano, aeroporto e slot são obrigatórios na reserva de vaga");
        }
        this.planoId = planoId;
        this.aeroportoCodigo = aeroportoCodigo;
        this.numeroVaga = numeroVaga;
        this.slot = slot;
    }

    public Long getPlanoId() { return planoId; }
    public String getAeroportoCodigo() { return aeroportoCodigo; }
    public int getNumeroVaga() { return numeroVaga; }
    public Slot getSlot() { return slot; }
}
