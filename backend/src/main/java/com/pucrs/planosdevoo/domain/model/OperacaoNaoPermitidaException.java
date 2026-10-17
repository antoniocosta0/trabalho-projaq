package com.pucrs.planosdevoo.domain.model;

// Operação que o estado atual do plano não permite, por exemplo alterar o
// percurso de um plano que já não está em edição.
public class OperacaoNaoPermitidaException extends RuntimeException {

    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
