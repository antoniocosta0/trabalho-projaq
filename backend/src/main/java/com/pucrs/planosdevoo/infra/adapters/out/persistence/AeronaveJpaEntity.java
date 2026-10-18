package com.pucrs.planosdevoo.infra.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Tabela única para todas as categorias: a coluna "categoria" diz qual é,
// e as colunas que não se aplicam à categoria ficam nulas.
@Entity
@Table(name = "aeronave")
public class AeronaveJpaEntity {

    @Id
    private String prefixo;

    @Column(nullable = false)
    private String categoria;

    @Column(name = "velocidade_cruzeiro_kmh", nullable = false)
    private double velocidadeCruzeiroKmh;

    @Column(name = "autonomia_km", nullable = false)
    private double autonomiaKm;

    @Column(name = "empresa_manutencao")
    private String empresaManutencao;

    @Column(name = "companhia_aerea")
    private String companhiaAerea;

    @Column(name = "capacidade_passageiros")
    private Integer capacidadePassageiros;

    @Column(name = "capacidade_toneladas")
    private Double capacidadeToneladas;

    protected AeronaveJpaEntity() { }

    public AeronaveJpaEntity(String prefixo, String categoria, double velocidadeCruzeiroKmh, double autonomiaKm,
                             String empresaManutencao, String companhiaAerea, Integer capacidadePassageiros,
                             Double capacidadeToneladas) {
        this.prefixo = prefixo;
        this.categoria = categoria;
        this.velocidadeCruzeiroKmh = velocidadeCruzeiroKmh;
        this.autonomiaKm = autonomiaKm;
        this.empresaManutencao = empresaManutencao;
        this.companhiaAerea = companhiaAerea;
        this.capacidadePassageiros = capacidadePassageiros;
        this.capacidadeToneladas = capacidadeToneladas;
    }

    public String getPrefixo() { return prefixo; }
    public String getCategoria() { return categoria; }
    public double getVelocidadeCruzeiroKmh() { return velocidadeCruzeiroKmh; }
    public double getAutonomiaKm() { return autonomiaKm; }
    public String getEmpresaManutencao() { return empresaManutencao; }
    public String getCompanhiaAerea() { return companhiaAerea; }
    public Integer getCapacidadePassageiros() { return capacidadePassageiros; }
    public Double getCapacidadeToneladas() { return capacidadeToneladas; }
}
