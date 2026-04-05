package org.banco.exceptions;

public class SaldoNaoZeradoException extends RuntimeException {

    public SaldoNaoZeradoException(Long id) {
        super(String.format("Não é possível excluir a conta '%d', pois ela possui saldo remanescente.", id));
    }
}
