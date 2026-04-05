package org.banco.model.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContaUpdateDTO {
    @Size(min = 1, message = "O nome do titular não pode estar em branco.")
    private String titular;

    @PositiveOrZero(message = "O campo 'saldo' não pode ser negativo.")
    private BigDecimal saldo;

    public boolean isNull() {
        return titular == null && saldo == null;
    }

}

