package com.pucrs.planosdevoo.domain.model;

import java.util.List;

// Plano que não pode ser aprovado. Carrega a lista completa de problemas, para
// a API dizer tudo o que está errado de uma vez.
public class PlanoInvalidoException extends RuntimeException {

    private final List<String> problemas;

    public PlanoInvalidoException(List<String> problemas) {
        super("O plano não pode ser aprovado: " + problemas.size() + " problema(s) encontrado(s)");
        this.problemas = List.copyOf(problemas);
    }

    public List<String> getProblemas() { return problemas; }
}
