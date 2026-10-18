package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.model.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.model.PlanoInvalidoException;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Traduz os erros do domínio para status HTTP.
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail naoEncontrado(RecursoNaoEncontradoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail regraViolada(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    // Ex.: alterar o percurso de um plano que não está mais em edição.
    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    public ProblemDetail operacaoNaoPermitida(OperacaoNaoPermitidaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    // Campo ausente ou inválido no corpo da requisição. Sem este tratamento a
    // resposta cai no handler padrão do Spring, que devolve stack trace.
    // O corpo traz a lista de problemas, um por campo, como o enunciado pede.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail corpoInvalido(MethodArgumentNotValidException e) {
        List<String> problemas = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        ProblemDetail detalhe = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Requisição inválida");
        detalhe.setProperty("problemas", problemas);
        return detalhe;
    }

    // Aprovação recusada: devolve a lista completa de problemas, não só o primeiro.
    @ExceptionHandler(PlanoInvalidoException.class)
    public ProblemDetail planoInvalido(PlanoInvalidoException e) {
        ProblemDetail detalhe = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        detalhe.setProperty("problemas", e.getProblemas());
        return detalhe;
    }

    // Duas aprovações disputaram a mesma altitude ou vaga e esta perdeu.
    @ExceptionHandler(ConflitoDeReservaException.class)
    public ProblemDetail conflitoDeReserva(ConflitoDeReservaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }
}
