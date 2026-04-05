package org.banco.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationErrorResponse(
        int status,
        String erro,
        String mensagem,
        List<Erros> campos,
        LocalDateTime timestamp
) {
    public ValidationErrorResponse(int status, String mensagem, List<Erros> campos) {
        this(status, "Erro de Validação", mensagem, campos, LocalDateTime.now());
    }
}