package org.banco.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.banco.model.dto.ContaRequestDTO;
import org.banco.model.dto.ContaResponseDTO;
import org.banco.model.dto.ContaUpdateDTO;
import org.banco.model.dto.StatusUpdateDTO;
import org.banco.service.ContaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contas")
@Tag(name = "Contas", description = "Gerenciamento de contas bancárias")
public class ContaController {
    private final ContaService contaService;

    @GetMapping
    @Operation(summary = "Listagem de contas", description = "Lista todas as contas da base")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),})
    public ResponseEntity<List<ContaResponseDTO>> listarTodasContas() {
        List<ContaResponseDTO> contas = contaService.listarTodasContas();
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca conta por ID", description = "Retorna dados da conta do ID informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<ContaResponseDTO> buscarContaPorId(@PathVariable Long id) {
        ContaResponseDTO conta = contaService.buscarContaPorId(id);
        return ResponseEntity.ok(conta);
    }

    @PostMapping
    @Operation(summary = "Criar nova conta", description = "Cadastra uma nova conta na base")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
            @ApiResponse(responseCode = "409", description = "Documento (CPF) já cadastrado")
    })
    public ResponseEntity<ContaResponseDTO> criarNovaConta(@Valid @RequestBody ContaRequestDTO contaDTO) {
        ContaResponseDTO novaConta = contaService.criarConta(contaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados conta", description = "Atualiza nome do titular e/ou saldo da conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Processamento realizado"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "400", description = "Nenhum campo fornecido ou dados inválidos")
    })
    public ResponseEntity<ContaResponseDTO> atualizarConta(@PathVariable Long id,
                                                           @RequestBody @Valid ContaUpdateDTO contaRequest) {
        ContaResponseDTO contaAtualizada = contaService.atualizarConta(id, contaRequest);
        return ResponseEntity.ok(contaAtualizada);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza status da conta", description = "Bloquear ou ativar conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Processamento realizado"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<ContaResponseDTO> atualizarStatusConta(@PathVariable Long id,
                                                                 @RequestBody @Valid StatusUpdateDTO statusConta) {
        ContaResponseDTO contaAtualizada = contaService.atualizarStatusConta(id, statusConta);
        return ResponseEntity.ok(contaAtualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir conta", description = "Exclui a conta do ID informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Não foi possível excluir a conta"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    public ResponseEntity<Void> excluirConta(@PathVariable Long id) {
        contaService.excluirConta(id);
        return ResponseEntity.noContent().build();
    }
}
