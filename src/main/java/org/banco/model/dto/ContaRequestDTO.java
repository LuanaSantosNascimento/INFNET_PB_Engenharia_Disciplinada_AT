package org.banco.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banco.model.enums.TipoConta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaRequestDTO {
    @NotBlank(message = "O campo 'titular' é obrigatório.")
    private String titular;

    @NotBlank(message = "O campo 'documento' é obrigatório.")
    private String documento;

    @NotNull(message = "O campo 'tipoConta' é obrigatório.")
    private TipoConta tipoConta;
}

