package com.pucrs.planosdevoo.domain.model;

// Uma altitude de uma aerovia ocupada em um slot, em nome de um plano aprovado.
public class ReservaAerovia {

    private final Long planoId;
    private final Aerovia aerovia;
    private final int altitude;
    private final Slot slot;

    public ReservaAerovia(Long planoId, Aerovia aerovia, int altitude, Slot slot) {
        if (planoId == null || aerovia == null || slot == null) {
            throw new IllegalArgumentException("Plano, aerovia e slot são obrigatórios na reserva de aerovia");
        }
        this.planoId = planoId;
        this.aerovia = aerovia;
        this.altitude = altitude;
        this.slot = slot;
    }

    public Long getPlanoId() { return planoId; }
    public Aerovia getAerovia() { return aerovia; }
    public int getAltitude() { return altitude; }
    public Slot getSlot() { return slot; }
}
