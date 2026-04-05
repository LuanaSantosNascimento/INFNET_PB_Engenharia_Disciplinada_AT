package org.banco.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.banco.model.enums.StatusConta;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDTO {
    @NotNull(message = "O campo 'status' é obrigatório.")
    private StatusConta status;

    @NotBlank(message = "O motivo da alteração é obrigatório")
    @Size(min = 10, max = 100, message = "O motivo deve ter entre 10 e 100 caracteres")
    private String motivo;
}

