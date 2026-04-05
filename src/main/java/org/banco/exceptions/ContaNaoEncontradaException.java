package org.banco.exceptions;

public class ContaNaoEncontradaException extends RuntimeException {

    public ContaNaoEncontradaException(Long id) {
        super(String.format("Conta com ID '%d' não encontrada", id));
    }
}
