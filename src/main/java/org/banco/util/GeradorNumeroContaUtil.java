package org.banco.util;

import java.util.Random;

public class GeradorNumeroContaUtil {

    private static final Random random = new Random();
    private static final int RANGE_CINCO_DIGITOS = 90000;
    private static final int LIMITE_DIGITO_VERIFICADOR = 9;
    private static final int VALOR_MINIMO_CINCO_DIGITOS = 10000;
    private static final String FORMATO_CONTA_BANCARIA = "%d-%d";

    public static String gerarNumeroConta() {

        int numeroConta = random.nextInt(RANGE_CINCO_DIGITOS) + VALOR_MINIMO_CINCO_DIGITOS;
        int digito = random.nextInt(LIMITE_DIGITO_VERIFICADOR);

        return String.format(FORMATO_CONTA_BANCARIA, numeroConta, digito);
    }
}
