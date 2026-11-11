package com.pucrs.planosdevoo.domain.exceptions;

public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
