package com.pucrs.cadastro.domain.exceptions;

public class RegistroDuplicadoException extends RuntimeException {

    public RegistroDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
