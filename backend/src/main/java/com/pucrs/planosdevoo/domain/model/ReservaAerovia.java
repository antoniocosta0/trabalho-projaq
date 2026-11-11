package com.pucrs.planosdevoo.domain.model;

public class ReservaAerovia {

    private final Long planoId;
    private final String aeroviaId;
    private final int altitude;
    private final Slot slot;

    public ReservaAerovia(Long planoId, String aeroviaId, int altitude, Slot slot) {
        if (planoId == null || aeroviaId == null || slot == null) {
            throw new IllegalArgumentException("Plano, aerovia e slot são obrigatórios na reserva de aerovia");
        }
        this.planoId = planoId;
        this.aeroviaId = aeroviaId;
        this.altitude = altitude;
        this.slot = slot;
    }

    public Long getPlanoId() { return planoId; }
    public String getAeroviaId() { return aeroviaId; }
    public int getAltitude() { return altitude; }
    public Slot getSlot() { return slot; }
}
