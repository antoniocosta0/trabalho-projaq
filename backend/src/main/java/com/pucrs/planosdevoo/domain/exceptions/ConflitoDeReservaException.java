package com.pucrs.planosdevoo.domain.exceptions;

// Altitude ou vaga que já pertence a outro plano. Só acontece na gravação
// quando duas aprovações disputam o mesmo recurso ao mesmo tempo.
public class ConflitoDeReservaException extends RuntimeException {

    public ConflitoDeReservaException(String mensagem) {
        super(mensagem);
    }
}
