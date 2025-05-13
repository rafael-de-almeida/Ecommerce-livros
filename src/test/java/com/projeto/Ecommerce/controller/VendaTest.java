package com.projeto.Ecommerce.controller;

import com.projeto.Ecommerce.service.ClientesService;
import com.projeto.Ecommerce.service.OrdemService;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VendaTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private OrdemService ordemService;


    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        System.out.println("[INFO] WebDriver initialized.");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("[INFO] WebDriver closed.");
        }
    }

    private void navigateToPage(String url) {
        driver.get(url);
        System.out.println("[INFO] Opened page: " + url);
    }
    private void preencherInput(String name, String value) {
        WebElement inputField = driver.findElement(By.name(name));
        inputField.sendKeys(value);
        System.out.println("[INFO] Filled input '" + name + "' with '" + value + "'.");
    }
    @Test
    @Order(1)
    void testFormSubmissionAndApiValidation() throws InterruptedException {

        // Navegar para a página de compra
        navigateToPage("http://127.0.0.1:5500/frontend-ecommerce-livro/TelaInicial.html?id=2");

        // Localize o botão "Comprar Agora" pelo seu identificador (exemplo usando className)
        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-comprar-agora")));

        // Realize o clique no botão
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", buyNowButton);  // Garante que o botão esteja visível
        Thread.sleep(500);  // Espera um tempo para garantir que o botão foi encontrado
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", buyNowButton);  // Clica no botão

        System.out.println("[INFO] Cliquei no botão 'Comprar Agora' com sucesso.");

        // Aguardar a navegação para a página de finalização de compra
        WebElement checarTituloCarrinho = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titulo-pagina-carrinho")));
        assert checarTituloCarrinho.getText().equals("Carrinho de Compras") : "A página de carrinho não foi carregada corretamente.";



        WebElement botaoFinalizarCompra = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn.btn-success.w-100.mt-2")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botaoFinalizarCompra);
        Thread.sleep(500);  // Pequena pausa para garantir visibilidade
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botaoFinalizarCompra);


        // Aguardar a navegação para a página de finalização de compra
        WebElement checarTituloPaginaFinalizarCompra = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titulo-pagina-finalizar-compra")));
        assert checarTituloPaginaFinalizarCompra.getText().equals("Finalizar Compra") : "A página de finalizar compra não foi carregada corretamente.";

        // Esperar o dropdown ficar visível
        WebElement enderecoDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("endereco-selecionado")));

        // Criar o Select e selecionar a terceira opção (índice 2, pois começa em 0)
        Select selectEndereco = new Select(enderecoDropdown);
        selectEndereco.selectByIndex(2); // seleciona a terceira opção

        System.out.println("[INFO] Endereço selecionado com sucesso.");


        WebElement cartaoDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartao1")));
        Select selectCartao = new Select(cartaoDropdown);
        selectCartao.selectByIndex(1);

        System.out.println("[INFO] Cartão selecionado com sucesso.");


        // Achar o grupo mais recente de cartão
        List<WebElement> grupos = driver.findElements(By.cssSelector("div.d-flex.align-items-center.mb-2.gap-2"));
        WebElement grupoMaisRecente = grupos.get(grupos.size() - 1);

        // Dentro do grupo, achar o input de valor
        WebElement inputValor = grupoMaisRecente.findElement(By.cssSelector("input[type='number']"));


        inputValor.sendKeys("105.90");
        System.out.println("[INFO] Valor preenchido no cartão.");


        WebElement checarBotaoFinalizarCompra = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finalizarCompraBtn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checarBotaoFinalizarCompra);
        Thread.sleep(500);  // Pequena pausa para garantir visibilidade
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checarBotaoFinalizarCompra);
        assert checarBotaoFinalizarCompra.getText().equals("Finalizar Compra") : "A botão de finalizar compra não foi localizado.";

        System.out.println("[INFO] Compra finalizada.");

    }
    @Test
    @Order (2)
    void alterarStatusVenda () throws InterruptedException {
        int numeroPedido = pegarUltimoPedidoTrocaSolicitado();
        navigateToPage("http://127.0.0.1:5500/pesquisarVendas.html");

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");

        Map<String, String> inputData;
        inputData = Map.of(
                //"nome", "teste",
                //"valor", "123",
                "numero-pedido", Integer.toString(numeroPedido),
                "status", "EM PROCESSAMENTO");

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-success")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button.");
        WebElement selectElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.status-select[data-id='"+ numeroPedido +"']")));
        Select select = new Select(selectElement);
        select.selectByValue("ENTREGUE");
        WebElement botaoSalvar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.salvar-status[data-id='"+ numeroPedido +"']")));
        botaoSalvar.click();
        System.out.println("[INFO] Clicked salvar button.");

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alert.accept();

        Thread.sleep(3000);

    }
    int pegarUltimoPedidoTrocaSolicitado(){
        navigateToPage("http://127.0.0.1:5500/historicoCompras.html?id=2");
        System.out.println("[INFO] Page opened successfully.");
        List<WebElement> cards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.card-body")));

        int maiorNumeroPedido = 0;

        for (WebElement card : cards) {
            try {
                // Verifica se o card tem o span com texto "Troca solicitada"
                WebElement span = card.findElement(By.cssSelector("span.badge"));
                if (span.getText().trim().equalsIgnoreCase("em processamento")) {

                    // Encontra o número do pedido
                    WebElement titulo = card.findElement(By.cssSelector("h5.card-title"));
                    String textoTitulo = titulo.getText(); // Ex: "Pedido #48"

                    Matcher matcher = Pattern.compile("#(\\d+)").matcher(textoTitulo);
                    if (matcher.find()) {
                        int numeroPedido = Integer.parseInt(matcher.group(1));
                        if (numeroPedido > maiorNumeroPedido) {
                            maiorNumeroPedido = numeroPedido;
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                // Ignora cards sem span ou título válidos
            }
        }
        return maiorNumeroPedido;
    }


    @Test
    @Order(3)
    void CupomJaResgatado() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/TelaInicial.html?id=2");

        // Localize o botão "Comprar Agora" pelo seu identificador (exemplo usando className)
        WebElement buyNowButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-comprar-agora")));

        // Realize o clique no botão
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", buyNowButton);  // Garante que o botão esteja visível
        Thread.sleep(500);  // Espera um tempo para garantir que o botão foi encontrado
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", buyNowButton);  // Clica no botão

        System.out.println("[INFO] Cliquei no botão 'Comprar Agora' com sucesso.");

        // Aguardar a navegação para a página de finalização de compra
        WebElement checarTituloCarrinho = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titulo-pagina-carrinho")));
        assert checarTituloCarrinho.getText().equals("Carrinho de Compras") : "A página de carrinho não foi carregada corretamente.";



        WebElement botaoFinalizarCompra = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn.btn-success.w-100.mt-2")
                )
        );

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botaoFinalizarCompra);
        Thread.sleep(500);  // Pequena pausa para garantir visibilidade
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botaoFinalizarCompra);


        // Aguardar a navegação para a página de finalização de compra
        WebElement checarTituloPaginaFinalizarCompra = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titulo-pagina-finalizar-compra")));
        assert checarTituloPaginaFinalizarCompra.getText().equals("Finalizar Compra") : "A página de finalizar compra não foi carregada corretamente.";

        // Esperar o dropdown ficar visível
        WebElement enderecoDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("endereco-selecionado")));

        // Criar o Select e selecionar a terceira opção (índice 2, pois começa em 0)
        Select selectEndereco = new Select(enderecoDropdown);
        selectEndereco.selectByIndex(2); // seleciona a terceira opção

        System.out.println("[INFO] Endereço selecionado com sucesso.");


        WebElement cartaoDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("cartao1")));
        Select selectCartao = new Select(cartaoDropdown);
        selectCartao.selectByIndex(1);

        System.out.println("[INFO] Cartão selecionado com sucesso.");


        // Achar o grupo mais recente de cartão
        List<WebElement> grupos = driver.findElements(By.cssSelector("div.d-flex.align-items-center.mb-2.gap-2"));
        WebElement grupoMaisRecente = grupos.get(grupos.size() - 1);

        // Dentro do grupo, achar o input de valor
        WebElement inputValor = grupoMaisRecente.findElement(By.cssSelector("input[type='number']"));


        inputValor.sendKeys("105.90");
        System.out.println("[INFO] Valor preenchido no cartão.");


        WebElement inputCupom = wait.until(ExpectedConditions.elementToBeClickable(By.id("input-cupom")));
        inputCupom.sendKeys(" TROCA-61-C11C49"); // substitua pelo código real do cupom

        WebElement aplicarCupomBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-aplicar-cupom")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", aplicarCupomBtn);
        System.out.println("[INFO] Cupom aplicado.");
        Thread.sleep(1000);

        WebElement checarBotaoFinalizarCompra = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finalizarCompraBtn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checarBotaoFinalizarCompra);
        Thread.sleep(500);  // Pequena pausa para garantir visibilidade
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checarBotaoFinalizarCompra);
        assert checarBotaoFinalizarCompra.getText().equals("Finalizar Compra") : "A botão de finalizar compra não foi localizado.";

        System.out.println("[INFO] Compra finalizada.");

        try {
            WebElement mensagemErro = driver.findElement(By.xpath("//*[contains(text(), 'Desconto(Cupom): R$0,00') and contains(text(), '0')]"));
            if (mensagemErro.isDisplayed()) {
                throw new AssertionError("Erro: Cupom já foi resgatado ou inválido (Desconto do cupom 0).");
            }
        } catch (NoSuchElementException e) {
        }

    }

}

