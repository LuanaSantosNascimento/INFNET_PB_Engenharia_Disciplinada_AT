package org.banco.exceptions;

public class ContaDuplicadaException extends RuntimeException {

    public ContaDuplicadaException(String documento) {
        super(String.format("Já existe uma conta vinculada ao documento informado: %s", documento));
    }
}
