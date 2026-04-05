package org.banco.service;

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
import org.banco.model.enums.TipoConta;
import org.banco.model.repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    @ParameterizedTest
    @DisplayName("Deve criar conta com sucesso para diferentes tipos")
    @CsvSource({"CORRENTE, 4567", "POUPANCA, 0123"})
    void deveCriarContaComSucesso(TipoConta tipo, String agenciaEsperada) {
        var request = new ContaRequestDTO("João Silva", "12345678901", tipo);
        when(contaRepository.existsByDocumento(anyString())).thenReturn(false);
        when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArguments()[0]);

        ContaResponseDTO response = contaService.criarConta(request);

        assertNotNull(response);
        assertEquals("João Silva", response.getTitular());
        assertEquals(agenciaEsperada, response.getAgencia());
        assertEquals(StatusConta.ATIVA, response.getStatus());
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar conta com documento duplicado")
    void deveLancarExcecaoContaDuplicadaException() {
        var request = new ContaRequestDTO("João", "123", TipoConta.CORRENTE);
        when(contaRepository.existsByDocumento("123")).thenReturn(true);

        assertThrows(ContaDuplicadaException.class, () -> contaService.criarConta(request));
        verify(contaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar conta por ID com sucesso")
    void deveBuscarContaPorId() {
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setTitular("Maria");
        when(contaRepository.findById(1L)).thenReturn(Optional.of(conta));

        ContaResponseDTO resultado = contaService.buscarContaPorId(1L);

        assertEquals("Maria", resultado.getTitular());
    }

    @Test
    @DisplayName("Deve listar todas as contas")
    void deveBuscarContas() {
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setTitular("Maria");
        when(contaRepository.findAll()).thenReturn(List.of(conta));

        List<ContaResponseDTO> resultado = contaService.listarTodasContas();

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.getFirst().getId());
        assertEquals("Maria", resultado.getFirst().getTitular());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoContaNaoEncontradaException() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ContaNaoEncontradaException.class, () -> contaService.buscarContaPorId(99L));
    }

    @ParameterizedTest(name = "Ao atualizar com saldo {0}, o resultadoado deve ser {0}")
    @CsvSource(
            value = {
                    "100.50, Ana Maria",
                    "2000.00, NULL",
                    "NULL, José Oliveira"},
            nullValues = "NULL"
    )
    @DisplayName("Deve atualizar titular e saldo com sucesso")
    void deveAtualizarConta(BigDecimal novoSaldo, String novoTitular) {
        Long id = 1L;
        Conta contaOriginal = new Conta();
        contaOriginal.setTitular("Antigo Titular");

        var updateDTO = new ContaUpdateDTO(novoTitular, novoSaldo);

        when(contaRepository.findById(id)).thenReturn(Optional.of(contaOriginal));
        when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArguments()[0]);

        ContaResponseDTO resultado = contaService.atualizarConta(id, updateDTO);
        String titularEsperado = (novoTitular != null) ? novoTitular : "Antigo Titular";
        assertEquals(titularEsperado, resultado.getTitular());
        assertEquals(novoSaldo, resultado.getSaldo());
    }

    @Test
    @DisplayName("Deve lançar ContaNaoEncontradaException ao tentar atualizar conta")
    void deveLancarContaNaoEncontradaExceptionAoAtualizarConta() {
        var updateDTO = new ContaUpdateDTO("Novo Titular", BigDecimal.TEN);

        when(contaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class, () -> contaService.atualizarConta(1L, updateDTO));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve lançar DadosInvalidosException ao tentar atualizar conta")
    void deveLancarDadosInvalidosExceptionAoAtualizarConta() {
        var updateDTO = new ContaUpdateDTO(null, null);
        Conta contaOriginal = new Conta();
        contaOriginal.setTitular("Antigo Titular");

        when(contaRepository.findById(1L)).thenReturn(Optional.of(contaOriginal));

        assertThrows(DadosInvalidosException.class, () -> contaService.atualizarConta(1L, updateDTO));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve atualizar status da conta com sucesso")
    void deveAtualizarStatus() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setStatus(StatusConta.ATIVA);
        var updateStatus = new StatusUpdateDTO(StatusConta.BLOQUEADA, "Suspeita de fraude");

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenAnswer(i -> i.getArguments()[0]);

        ContaResponseDTO resultado = contaService.atualizarStatusConta(id, updateStatus);

        assertEquals(StatusConta.BLOQUEADA, resultado.getStatus());
        assertEquals("Suspeita de fraude", resultado.getUltimoMotivoAlteracao());
    }

    @Test
    @DisplayName("Deve lançar erro ContaNaoEncontradaException ao atualizar status da conta")
    void deveLancarErroContaNaoEncontradaException() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setStatus(StatusConta.ATIVA);
        var updateStatus = new StatusUpdateDTO(StatusConta.BLOQUEADA, "Suspeita de fraude");

        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class, () -> contaService.atualizarStatusConta(id, updateStatus));
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve excluir conta com saldo zerado")
    void deveExcluirContaComSucesso() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setSaldo(BigDecimal.ZERO);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertDoesNotThrow(() -> contaService.excluirConta(id));
        verify(contaRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve laçar exception SaldoNaoZeradoException ao tentar excluir conta com saldo remanescente")
    void deveLancarExcecaoSaldoNaoZeradoException() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setSaldo(new BigDecimal("10.00"));

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        assertThrows(SaldoNaoZeradoException.class, () -> contaService.excluirConta(id));
        verify(contaRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve laçar exception ContaNaoEncontradaException ao tentar excluir conta")
    void deveLancarContaNaoEncontradaExceptionAoTentarExcluirConta() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setSaldo(new BigDecimal("10.00"));

        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ContaNaoEncontradaException.class, () -> contaService.excluirConta(id));
        verify(contaRepository, never()).deleteById(anyLong());
    }
}