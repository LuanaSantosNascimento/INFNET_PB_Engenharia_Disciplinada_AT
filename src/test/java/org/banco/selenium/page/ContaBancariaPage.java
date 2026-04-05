package org.banco.selenium.page;

import org.banco.model.enums.TipoConta;
import org.banco.selenium.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class ContaBancariaPage extends BasePage {

    private final By inputNome = By.id("nome");
    private final By inputDocumento = By.id("documento");
    private final By inputTipoConta = By.id("tipoConta");
    private final By btnCriarConta = By.xpath("//form[@id='form-criar']//button");

    private final By inputEditarNome = By.id("editar-nome");
    private final By inputEditarSaldo = By.id("editar-saldo");
    private final By btnSalvarEdicao = By.id("btn-salvar-edicao");

    private final By btnConfirmarExclusao = By.id("btn-confirmar-excluir");

    private final By alertaPrincipal = By.id("main-alert");
    private final By mensagemAlerta = By.id("alert-message");
    private final By btnFecharAlerta = By.className("btn-close");

    public ContaBancariaPage(WebDriver driver) {
        super(driver);
    }

    public void criarConta(String titular, String documento, TipoConta tipoConta) {
        String nomeNormalizado = getNomeTitularNormalizado(titular);

        new Select(driver.findElement(inputTipoConta)).selectByValue(tipoConta.toString());
        type(inputNome, titular);
        type(inputDocumento, documento);

        takeScreenshot("0.Criar_Conta_Preenchimento_" + nomeNormalizado);
        click(btnCriarConta);
        takeScreenshot("0.Conta_Criada_" + nomeNormalizado);
    }

    public void clicarBotaoEditarConta(String titular) {
        clicarBotaoNaLinha(titular, "Editar");
        takeScreenshot("1.Modal_Editar_Conta_" + getNomeTitularNormalizado(titular));
    }

    public void editarConta(String nomeInicial, String novoNome, String novoSaldo) {
        String nomeNormalizado = getNomeTitularNormalizado(nomeInicial);

        WebElement inputNomeEdicao = wait.until(ExpectedConditions.elementToBeClickable(inputEditarNome));
        inputNomeEdicao.clear();
        inputNomeEdicao.sendKeys(novoNome);

        WebElement inputSaldoEdicao = driver.findElement(inputEditarSaldo);
        inputSaldoEdicao.clear();
        inputSaldoEdicao.sendKeys(novoSaldo);

        takeScreenshot("1.Modal_Editar_Dados_Preenchidos_" + nomeNormalizado);
        click(btnSalvarEdicao);

        String xpathNovoNome = String.format(
                "//table[@id='tabela-contas']//td/strong[normalize-space()='%s']",
                novoNome
        );
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathNovoNome)));

        takeScreenshot("1.Edicao_Finalizada_Sucesso_" + nomeNormalizado);
    }

    public void clicarBotaoExcluirConta(String titular) {
        clicarBotaoNaLinha(titular, "Excluir");
        takeScreenshot("2.Modal_Excluir_Conta_" + getNomeTitularNormalizado(titular));
    }

    public boolean excluirConta(String titular) {
        WebElement linha = getLinhaDaConta(titular);
        click(btnConfirmarExclusao);

        boolean excluiu = wait.until(ExpectedConditions.stalenessOf(linha));
        takeScreenshot("2.Exclusao_Confirmada_" + getNomeTitularNormalizado(titular));
        return excluiu;
    }

    public String capturarMensagemAlertaErro(String titular) {
        click(btnConfirmarExclusao);
        takeScreenshot("2.Exclusao_Erro_Saldo_" + getNomeTitularNormalizado(titular));

        WebElement alerta = wait.until(ExpectedConditions.visibilityOfElementLocated(alertaPrincipal));
        String texto = wait.until(
                ExpectedConditions.presenceOfNestedElementLocatedBy(alerta, mensagemAlerta)
        ).getText();

        alerta.findElement(btnFecharAlerta).click();
        wait.until(ExpectedConditions.stalenessOf(alerta));

        return texto;
    }

    public boolean isContaNaTabela(String nomeConta) {
        try {
            return getLinhaDaConta(nomeConta).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    public WebElement getLinhaDaConta(String nomeConta) {
        String xpath = String.format("//table[@id='tabela-contas']//tr[td/strong[normalize-space()='%s']]", nomeConta);
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    private void clicarBotaoNaLinha(String titular, String textoBotao) {
        String xpath = String.format(
                "//tr[td/strong[normalize-space()='%s']]//button[contains(text(), '%s')]",
                titular, textoBotao
        );
        WebElement botao = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", botao);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botao);
    }

    public String getMensagemDeValidacaoDoInputNome() {
        return getInputValidationMessage(inputNome);
    }

    public String getMensagemDeValidacaoInputDocumento() {
        return getInputValidationMessage(inputDocumento);
    }

    private String getNomeTitularNormalizado(String titular) {
        if (titular == null || titular.trim().isEmpty()) return "NOME_INVALIDO";
        return titular.trim().replaceAll("\\s+", "_");
    }
}