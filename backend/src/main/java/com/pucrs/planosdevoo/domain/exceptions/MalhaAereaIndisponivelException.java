package com.pucrs.planosdevoo.domain.exceptions;

public class MalhaAereaIndisponivelException extends RuntimeException {

    public MalhaAereaIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
