package org.banco.exceptions;

public class DadosInvalidosException extends IllegalArgumentException {

    public DadosInvalidosException() {
        super("Para seguir com a atualização, pelo menos um campo deve ser preenchido.");
    }
}
