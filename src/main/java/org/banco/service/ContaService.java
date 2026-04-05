package org.banco.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.banco.exceptions.ContaDuplicadaException;
import org.banco.exceptions.ContaNaoEncontradaException;
import org.banco.exceptions.DadosInvalidosException;
import org.banco.exceptions.SaldoNaoZeradoException;
import org.banco.model.dto.ContaRequestDTO;
import org.banco.model.dto.ContaResponseDTO;
import org.banco.model.dto.ContaUpdateDTO;
import org.banco.model.dto.StatusUpdateDTO;
import org.banco.model.entity.Conta;
import org.banco.model.enums.StatusConta;
import org.banco.model.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.banco.util.GeradorNumeroContaUtil.gerarNumeroConta;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    @Transactional
    public ContaResponseDTO criarConta(ContaRequestDTO requestDTO) {

        if (contaRepository.existsByDocumento(requestDTO.getDocumento())) {
            throw new ContaDuplicadaException(requestDTO.getDocumento());
        }

        Conta conta = new Conta();
        conta.setDocumento(requestDTO.getDocumento());
        conta.setTitular(requestDTO.getTitular());
        conta.setTipoConta(requestDTO.getTipoConta());
        conta.setSaldo(BigDecimal.ZERO);

        conta.setNumeroConta(gerarNumeroConta());
        conta.setAgencia(requestDTO.getTipoConta().getAgencia());
        conta.setStatus(StatusConta.ATIVA);

        Conta contaSalva = contaRepository.save(conta);
        return ContaResponseDTO.fromEntity(contaSalva);
    }

    @Transactional(readOnly = true)
    public ContaResponseDTO buscarContaPorId(Long id) {

        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));

        return ContaResponseDTO.fromEntity(conta);
    }

    @Transactional(readOnly = true)
    public List<ContaResponseDTO> listarTodasContas() {

        List<Conta> contas = contaRepository.findAll();

        return contas.stream()
                .map(ContaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public ContaResponseDTO atualizarStatusConta(Long id, StatusUpdateDTO novoStatus) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));

        log.info("Conta {} alterada de {} para {}. Motivo: {}\n",
                id, conta.getStatus(), novoStatus.getStatus(), novoStatus.getMotivo());

        conta.setStatus(novoStatus.getStatus());
        conta.setUltimoMotivoAlteracao(novoStatus.getMotivo());
        return ContaResponseDTO.fromEntity(contaRepository.save(conta));
    }

    @Transactional
    public ContaResponseDTO atualizarConta(Long id, ContaUpdateDTO contaRequest) {

        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));

        if (contaRequest.isNull()) {
            throw new DadosInvalidosException();
        }

        if (contaRequest.getTitular() != null) conta.setTitular(contaRequest.getTitular());
        if (contaRequest.getSaldo() != null) conta.setSaldo(contaRequest.getSaldo());

        Conta contaAtualizada = contaRepository.save(conta);
        return ContaResponseDTO.fromEntity(contaAtualizada);
    }

    @Transactional
    public void excluirConta(Long id) {

        Optional<Conta> conta = contaRepository.findById(id);

        if (conta.isEmpty()) {
            throw new ContaNaoEncontradaException(id);
        }

        if (conta.get().getSaldo().compareTo(BigDecimal.ZERO) > 0) {
            throw new SaldoNaoZeradoException(id);
        }
        contaRepository.deleteById(id);
    }
}
