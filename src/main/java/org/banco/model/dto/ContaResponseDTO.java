package org.banco.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banco.model.entity.Conta;
import org.banco.model.enums.StatusConta;
import org.banco.model.enums.TipoConta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaResponseDTO {

    private Long id;
    private BigDecimal saldo;
    private String agencia;
    private String titular;
    private String documento;
    private String numeroConta;
    private String ultimoMotivoAlteracao;
    private StatusConta status;
    private TipoConta tipoConta;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static ContaResponseDTO fromEntity(Conta conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getSaldo(),
                conta.getAgencia(),
                conta.getTitular(),
                conta.getDocumento(),
                conta.getNumeroConta(),
                conta.getUltimoMotivoAlteracao(),
                conta.getStatus(),
                conta.getTipoConta(),
                conta.getDataCriacao(),
                conta.getDataAtualizacao()
        );
    }
}

