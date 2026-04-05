package org.banco.exceptions;

import org.banco.model.dto.ErrorResponse;
import org.banco.model.dto.Erros;
import org.banco.model.dto.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ContaNaoEncontradaException.class)
    public ResponseEntity<Object> handleContaNaoEncontradaException(ContaNaoEncontradaException ex) {

        var error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Conta não encontrada", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ContaDuplicadaException.class)
    public ResponseEntity<Object> handleContaDuplicadaException(ContaDuplicadaException ex) {
        var error = new ErrorResponse(HttpStatus.CONFLICT.value(), "Conta duplicada.", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Object> handleDadosInvalidosException(DadosInvalidosException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Dados inválidos.",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(SaldoNaoZeradoException.class)
    public ResponseEntity<Object> handleSaldoNaoZeradoException(SaldoNaoZeradoException ex) {
        var error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Exclusão de conta com saldo.",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<Erros> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new Erros(f.getField(), f.getDefaultMessage()))
                .toList();

        var error = new ValidationErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Um ou mais campos estão inválidos",
                erros
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        var error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

