package com.pucrs.planosdevoo.domain.model;

// Uma vaga de um aeroporto ocupada em um slot, em nome de um plano aprovado.
public class ReservaVaga {

    private final Long planoId;
    private final Aeroporto aeroporto;
    private final int numeroVaga;
    private final Slot slot;

    public ReservaVaga(Long planoId, Aeroporto aeroporto, int numeroVaga, Slot slot) {
        if (planoId == null || aeroporto == null || slot == null) {
            throw new IllegalArgumentException("Plano, aeroporto e slot são obrigatórios na reserva de vaga");
        }
        this.planoId = planoId;
        this.aeroporto = aeroporto;
        this.numeroVaga = numeroVaga;
        this.slot = slot;
    }

    public Long getPlanoId() { return planoId; }
    public Aeroporto getAeroporto() { return aeroporto; }
    public int getNumeroVaga() { return numeroVaga; }
    public Slot getSlot() { return slot; }
}
