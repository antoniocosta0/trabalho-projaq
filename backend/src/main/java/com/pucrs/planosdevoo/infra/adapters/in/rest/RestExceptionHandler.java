package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.exceptions.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.exceptions.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.exceptions.PlanoInvalidoException;
import com.pucrs.planosdevoo.domain.exceptions.RecursoNaoEncontradoException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(PlanoInvalidoException.class)
    public ProblemDetail planoInvalido(PlanoInvalidoException e) {
        return comProblemas(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e.getProblemas());
    }

    @ExceptionHandler({OperacaoNaoPermitidaException.class, ConflitoDeReservaException.class})
    public ProblemDetail conflito(RuntimeException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail registroDuplicado(DataIntegrityViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, "O registro conflita com um que já existe");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail corpoInvalido(MethodArgumentNotValidException e) {
        List<String> problemas = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return comProblemas(HttpStatus.BAD_REQUEST, "Requisição inválida", problemas);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail parametroInvalido(MethodArgumentTypeMismatchException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Valor '" + e.getValue() + "' inválido para o parâmetro " + e.getName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail corpoIlegivel(HttpMessageNotReadableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "JSON inválido ou data fora do formato ISO-8601 (ex.: 2026-10-19T08:00:00)");
    }

    private static ProblemDetail comProblemas(HttpStatus status, String detalhe, List<String> problemas) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setProperty("problemas", problemas);
        return problema;
    }
}
