package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.Set;

public class AeronaveParticular extends Aeronave {

    private static final Set<Integer> ALTITUDES_PERMITIDAS = Set.of(25_000, 26_000, 27_000);

    private final String empresaManutencao;

    public AeronaveParticular(String prefixo, double velocidadeCruzeiroKmh, double autonomiaKm,
                              String empresaManutencao) {
        super(prefixo, velocidadeCruzeiroKmh, autonomiaKm);
        this.empresaManutencao = empresaManutencao;
    }

    @Override
    public boolean podeOperar(int altitude, LocalDateTime entrada, LocalDateTime saida) {
        return ALTITUDES_PERMITIDAS.contains(altitude);
    }

    @Override
    public String getCategoria() { return "PARTICULAR"; }

    public String getEmpresaManutencao() { return empresaManutencao; }
}
