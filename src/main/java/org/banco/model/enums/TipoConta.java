package org.banco.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TipoConta {
    POUPANCA("0123"),
    CORRENTE("4567");

    private String agencia;
}
