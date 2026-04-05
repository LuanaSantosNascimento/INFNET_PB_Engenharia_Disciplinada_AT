package org.banco.model.dto;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String erro, String mensagem, LocalDateTime timestamp) {
    public ErrorResponse(int status, String erro, String mensagem) {
        this(status, erro, mensagem, LocalDateTime.now());
    }
}
