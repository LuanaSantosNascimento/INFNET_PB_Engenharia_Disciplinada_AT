package org.banco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.banco.exceptions.ContaDuplicadaException;
import org.banco.exceptions.ContaNaoEncontradaException;
import org.banco.exceptions.DadosInvalidosException;
import org.banco.exceptions.GlobalExceptionHandler;
import org.banco.exceptions.SaldoNaoZeradoException;
import org.banco.model.dto.ContaRequestDTO;
import org.banco.model.dto.ContaResponseDTO;
import org.banco.model.dto.ContaUpdateDTO;
import org.banco.model.dto.StatusUpdateDTO;
import org.banco.model.enums.StatusConta;
import org.banco.model.enums.TipoConta;
import org.banco.service.ContaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContaController.class)
@Import(GlobalExceptionHandler.class)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContaService contaService;

    @Test
    @DisplayName("GET /contas - deve retornar lista de contas")
    void deveListarTodasContas() throws Exception {
        var contas = buildContaResponseDTOS();
        when(contaService.listarTodasContas()).thenReturn(contas);

        mockMvc.perform(get("/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].titular", is("Alana Oliveira")))
                .andExpect(jsonPath("$[0].saldo", is(500.00)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].titular", is("Angelino Albuquerque")))
                .andExpect(jsonPath("$[1].saldo", is(200.0)));

        verify(contaService, times(1)).listarTodasContas();
    }


    @Test
    @DisplayName("GET /contas/{id} - deve retornar conta existente")
    void deveBuscarContaPorId() throws Exception {
        var conta = buildContaResponseDTOS().getFirst();
        when(contaService.buscarContaPorId(1L)).thenReturn(conta);

        mockMvc.perform(get("/contas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titular", is("Alana Oliveira")))
                .andExpect(jsonPath("$.saldo", is(500.00)));

        verify(contaService, times(1)).buscarContaPorId(1L);
    }

    @Test
    @DisplayName("GET /contas/{id} - deve retornar 404 se não encontrada")
    void deveBuscarContaPorId_naoEncontrada() throws Exception {
        when(contaService.buscarContaPorId(99L)).thenThrow(new ContaNaoEncontradaException(99L));

        mockMvc.perform(get("/contas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro", containsString("Conta não encontrada")))
                .andExpect(jsonPath("$.mensagem", containsString("99")));

        verify(contaService, times(1)).buscarContaPorId(99L);
    }

    @Test
    @DisplayName("Deve atualizar status com sucesso")
    void deveAtualizarStatusComSucesso() throws Exception {
        var request = new StatusUpdateDTO(StatusConta.BLOQUEADA, "Bloqueio por fraude");
        when(contaService.atualizarStatusConta(1L, request)).thenReturn(buildContaResponseDTOS().getFirst());

        mockMvc.perform(patch("/contas/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(contaService, times(1)).atualizarStatusConta(1L, request);
    }

    @Test
    @DisplayName("Deve atualizar dados conta com sucesso")
    void deveAtualizarDadosContaComSucesso() throws Exception {
        var request = new ContaUpdateDTO("Alana Oliveira Silva", new BigDecimal("600.00"));
        when(contaService.atualizarConta(1L, request)).thenReturn(buildContaResponseDTOS().getFirst());

        mockMvc.perform(put("/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(contaService, times(1)).atualizarConta(1L, request);
    }

    @Test
    @DisplayName("Deve retornar 400 ao atualizar conta com dados inválidos")
    void deveAtualizarDadosConta_dadosInvalidos() throws Exception {
        var request = new ContaUpdateDTO(null, null);
        doThrow(new DadosInvalidosException()).when(contaService).atualizarConta(1L, request);

        mockMvc.perform(put("/contas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(contaService, times(1)).atualizarConta(1L, request);
    }


    @Test
    @DisplayName("Deve retornar 422 ao criar conta com dados inválidos")
    void deveRetornar422AoCriarContaInvalida() throws Exception {
        var requestInvalido = new ContaRequestDTO("", "", TipoConta.POUPANCA);

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erro").value("Erro de Validação"))
                .andExpect(jsonPath("$.campos").isArray());

        verify(contaService, times(0)).criarConta(any());
    }

    @Test
    @DisplayName("Deve criar conta com sucesso")
    void deveCriarContaComSucesso() throws Exception {
        var request = new ContaRequestDTO("Teste Criacao", "122121212", TipoConta.POUPANCA);
        when(contaService.criarConta(request)).thenReturn(buildContaResponseDTOS().getFirst());

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(contaService, times(1)).criarConta(request);
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar criar conta com cpf duplicado")
    void criarConta_cpfDuplicado() throws Exception {
        var request = new ContaRequestDTO("Teste Criacao", "122121212", TipoConta.POUPANCA);
        doThrow(new ContaDuplicadaException("122121212")).when(contaService).criarConta(request);

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(contaService, times(1)).criarConta(request);
    }

    @Test
    @DisplayName("DELETE /contas/{id} - deve excluir conta")
    void excluirConta() throws Exception {
        Mockito.doNothing().when(contaService).excluirConta(3L);
        mockMvc.perform(delete("/contas/3"))
                .andExpect(status().isNoContent());

        verify(contaService, times(1)).excluirConta(3L);
    }

    @Test
    @DisplayName("DELETE /contas/{id} - deve retornar 404 se não encontrada")
    void excluirConta_naoEncontrada() throws Exception {
        doThrow(new ContaNaoEncontradaException(99L)).when(contaService).excluirConta(99L);
        mockMvc.perform(delete("/contas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro", containsString("Conta não encontrada")))
                .andExpect(jsonPath("$.mensagem", containsString("99")));

        verify(contaService, times(1)).excluirConta(99L);
    }

    @Test
    @DisplayName("DELETE /contas/{id} - deve retornar 400 ao tentar excluir conta com saldo")
    void excluirConta_saldoRemanescente() throws Exception {
        doThrow(new SaldoNaoZeradoException(99L)).when(contaService).excluirConta(99L);
        mockMvc.perform(delete("/contas/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro", containsString("Exclusão de conta com saldo")))
                .andExpect(jsonPath("$.mensagem", containsString("99")));

        verify(contaService, times(1)).excluirConta(99L);
    }

    @Test
    @DisplayName("DELETE /contas/{id} - deve retornar 500 ao tentar excluir conta")
    void excluirConta_erroInterno() throws Exception {
        doThrow(new NullPointerException()).when(contaService).excluirConta(99L);
        mockMvc.perform(delete("/contas/99"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro", containsString("Ocorreu um erro inesperado")));

        verify(contaService, times(1)).excluirConta(99L);
    }

    private static List<ContaResponseDTO> buildContaResponseDTOS() {
        return List.of(
                new ContaResponseDTO(
                        1L,
                        new BigDecimal("500.00"),
                        "0123",
                        "Alana Oliveira",
                        "12345678900",
                        "12345-6",
                        "Abertura de conta",
                        StatusConta.ATIVA,
                        TipoConta.CORRENTE,
                        LocalDateTime.now(),
                        null
                ),

                new ContaResponseDTO(
                        2L,
                        new BigDecimal("200.00"),
                        "4567",
                        "Angelino Albuquerque",
                        "12345678900",
                        "12345-6",
                        "Bloqueio por inadimplencia",
                        StatusConta.BLOQUEADA,
                        TipoConta.CORRENTE,
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now()
                )
        );
    }
}
