package org.banco.selenium.test;

import org.banco.model.enums.TipoConta;
import org.banco.selenium.core.BaseTest;
import org.banco.selenium.page.ContaBancariaPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ContaBancariaSeleniumTest extends BaseTest {

    private ContaBancariaPage paginaPrincipal;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        paginaPrincipal = new ContaBancariaPage(driver);
        driver.get("http://localhost:" + port + "/banco-api/index.html");
    }

    @ParameterizedTest(name = "Titular={0}, Documento={1}, Tipo Conta={2}")
    @CsvSource({
            "Lucas Almeida, 23456789013, POUPANCA",
            "Maria Anastacia, 23456789014, POUPANCA",
            "Carlos Santos, 23456789015, POUPANCA"
    })
    @DisplayName("Deve criar conta com sucesso.")
    void deveCriarContaComSucesso(String nomeTitular, String documento, TipoConta tipoConta) {
        paginaPrincipal.criarConta(nomeTitular, documento, tipoConta);

        assertTrue(paginaPrincipal.isContaNaTabela(nomeTitular));
    }

    @Test
    @DisplayName("Deve exibir mensagem de erro ao tentar criar conta sem informar nome.")
    void naoDeveCriarContaComNomeNulo() {
        paginaPrincipal.criarConta("", "12222222", TipoConta.POUPANCA);
        String mensagemValidacao = paginaPrincipal.getMensagemDeValidacaoDoInputNome();
        assertEquals("Preencha este campo.", mensagemValidacao);
    }

    @Test
    @DisplayName("Deve exibir mensagem de erro ao tentar criar conta sem informar cpf")
    void naoDeveCriarContaComSaldoNegativo() {
        paginaPrincipal.criarConta("Maria ALice", "", TipoConta.POUPANCA);
        String mensagemValidacao = paginaPrincipal.getMensagemDeValidacaoInputDocumento();
        assertEquals("Preencha este campo.", mensagemValidacao);
    }

    @ParameterizedTest(name = "Titular={0}, SaldoInicial={1}, SaldoFinal={2}")
    @CsvSource({
            "Eliete Oliveira,11111111111, 2, Eliete O Silva",
            "Alberto Martins,22222222222, 5000, Humberto Martins",
            "Juarez Santos,333333333333, 0, Juarez Santos OLiveira"
    })
    @DisplayName("Deve editar conta com sucesso.")
    void deveEditarContaComSucesso(String titular, String documento, String saldoFinal, String nomeTitularAlterado) {
        paginaPrincipal.criarConta(titular, documento, TipoConta.CORRENTE);
        paginaPrincipal.clicarBotaoEditarConta(titular);
        paginaPrincipal.editarConta(titular, nomeTitularAlterado, saldoFinal);
        assertTrue(paginaPrincipal.isContaNaTabela(nomeTitularAlterado));
    }

    @ParameterizedTest(name = "Titular={0}")
    @CsvSource({"Pedro Oliveira", "Carlos Lima", "Ana Costa"})
    @DisplayName("Deve excluir conta com sucesso.")
    void deveExcluirContaComSucesso(String titular) {

        paginaPrincipal.clicarBotaoExcluirConta(titular);
        boolean resultado = paginaPrincipal.excluirConta(titular);
        assertTrue(resultado);
    }

    @ParameterizedTest
    @ValueSource(strings = {"João Silva", "Maria Santos"})
    void naoDeveExcluirContaComSaldo(String titular) {

        paginaPrincipal.clicarBotaoExcluirConta(titular);
        String mensagemReal = paginaPrincipal.capturarMensagemAlertaErro(titular);

        assertTrue(mensagemReal.contains("Não é possível excluir a conta"));
        assertTrue(mensagemReal.contains("pois ela possui saldo remanescente."));
        assertTrue(paginaPrincipal.isContaNaTabela(titular));
    }
}
