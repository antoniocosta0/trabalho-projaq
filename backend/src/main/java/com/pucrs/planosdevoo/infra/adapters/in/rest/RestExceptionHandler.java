package com.pucrs.planosdevoo.infra.adapters.in.rest;

import com.pucrs.planosdevoo.domain.model.ConflitoDeReservaException;
import com.pucrs.planosdevoo.domain.model.OperacaoNaoPermitidaException;
import com.pucrs.planosdevoo.domain.model.PlanoInvalidoException;
import com.pucrs.planosdevoo.domain.model.RecursoNaoEncontradoException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Traduz os erros do domínio e os da borda HTTP para Problem Detail (RFC 7807).
// Nenhuma resposta de erro leva stack trace: o que não estiver tratado aqui cai
// no handler genérico do fim do arquivo.
@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

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

    // ---------- erros da borda HTTP ----------

    // JSON malformado, ou data fora do formato ISO. Sem isto, a resposta vinha
    // do handler padrão do Spring, com stack trace no corpo.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail corpoIlegivel(HttpMessageNotReadableException e) {
        ProblemDetail detalhe = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Corpo da requisição inválido ou ilegível");
        detalhe.setProperty("problemas", List.of(
                "Confira se o JSON está bem formado e se as datas estão no formato ISO-8601, "
                        + "como 2026-10-19T08:00:00"));
        return detalhe;
    }

    // Tipo errado no path ou na query: /planos/abc, ?situacao=VOANDO.
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tipoInvalido(MethodArgumentTypeMismatchException e) {
        ProblemDetail detalhe = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Valor inválido para o parâmetro '" + e.getName() + "'");
        detalhe.setProperty("problemas", List.of(e.getName() + ": " + descreverEsperado(e)));
        return detalhe;
    }

    private static String descreverEsperado(MethodArgumentTypeMismatchException e) {
        Class<?> tipo = e.getRequiredType();
        if (tipo != null && tipo.isEnum()) {
            String valores = Arrays.stream(tipo.getEnumConstants()).map(Object::toString)
                    .collect(Collectors.joining(", "));
            return "valor '" + e.getValue() + "' não existe. Use um de: " + valores;
        }
        String esperado = tipo == null ? "outro tipo" : tipo.getSimpleName();
        return "valor '" + e.getValue() + "' não é um " + esperado + " válido";
    }

    // Falta ?inicio= ou ?fim= nas consultas de disponibilidade.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail parametroAusente(MissingServletRequestParameterException e) {
        ProblemDetail detalhe = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Parâmetro obrigatório ausente");
        detalhe.setProperty("problemas", List.of(e.getParameterName() + ": é obrigatório"));
        return detalhe;
    }

    // Unicidade violada no banco: dois cadastros simultâneos do mesmo código,
    // prefixo ou id. A conferência prévia do caso de uso não fecha essa janela.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail integridadeViolada(DataIntegrityViolationException e) {
        log.warn("Violação de integridade no banco", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "O registro conflita com um que já existe. Confira o código, o prefixo ou o identificador.");
    }

    // Rede de segurança: qualquer coisa não prevista vira 500 com corpo limpo.
    // O stack trace vai para o log do servidor, nunca para a resposta.
    @ExceptionHandler(Exception.class)
    public ProblemDetail erroInesperado(Exception e) {
        log.error("Erro não tratado", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno ao processar a requisição");
    }
}
