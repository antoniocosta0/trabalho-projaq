package com.pucrs.planosdevoo.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Slot {

    private final LocalDateTime inicio;

    public Slot(LocalDateTime inicio) {
        if (inicio == null) {
            throw new IllegalArgumentException("Início do slot é obrigatório");
        }
        this.inicio = inicio.withMinute(0).withSecond(0).withNano(0);
    }

    public static List<Slot> entre(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Início e fim são obrigatórios");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("Fim deve ser posterior ao início");
        }
        LocalDateTime cursor = new Slot(inicio).inicio;
        LocalDateTime ultimo = new Slot(fim).inicio;
        if (!fim.isAfter(ultimo)) {
            ultimo = ultimo.minusHours(1);
        }
        List<Slot> slots = new ArrayList<>();
        for (LocalDateTime atual = cursor; !atual.isAfter(ultimo); atual = atual.plusHours(1)) {
            slots.add(new Slot(atual));
        }
        return List.copyOf(slots);
    }

    public LocalDateTime getInicio() { return inicio; }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Slot slot)) {
            return false;
        }
        return inicio.equals(slot.inicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio);
    }
}
