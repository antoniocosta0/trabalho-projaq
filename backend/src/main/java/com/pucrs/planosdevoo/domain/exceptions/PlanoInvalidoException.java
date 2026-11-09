package com.pucrs.planosdevoo.domain.exceptions;

import java.util.List;

public class PlanoInvalidoException extends RuntimeException {

    private final List<String> problemas;

    public PlanoInvalidoException(List<String> problemas) {
        super("O plano não pode ser aprovado: " + problemas.size() + " problema(s) encontrado(s)");
        this.problemas = List.copyOf(problemas);
    }

    public List<String> getProblemas() { return problemas; }
}
