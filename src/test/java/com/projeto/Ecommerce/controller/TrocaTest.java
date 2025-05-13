package com.projeto.Ecommerce.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class TrocaTest {
    private WebDriver driver;
    private WebDriverWait wait;
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
    void testFazerPedidoDeTroca() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/historicoCompras.html?id=2");
        System.out.println("[INFO] Page opened successfully.");

        List<WebElement> cards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.card-body")));

        int maiorNumeroPedido = 0;
        WebElement cardComMaiorPedido = null;

        for (WebElement card : cards) {
            try {
                WebElement span = card.findElement(By.cssSelector("span.badge"));
                if (span.getText().trim().equalsIgnoreCase("Entregue")) {

                    WebElement titulo = card.findElement(By.cssSelector("h5.card-title"));
                    String textoTitulo = titulo.getText(); // Ex: "Pedido #48"

                    Matcher matcher = Pattern.compile("#(\\d+)").matcher(textoTitulo);
                    if (matcher.find()) {
                        int numeroPedido = Integer.parseInt(matcher.group(1));
                        if (numeroPedido > maiorNumeroPedido) {
                            maiorNumeroPedido = numeroPedido;
                            cardComMaiorPedido = card; // Salva o card
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                // Ignora cards sem span ou título válidos
            }
        }

        if (cardComMaiorPedido != null) {
            WebElement botaoTroca = cardComMaiorPedido.findElement(By.cssSelector("button.btn.btn-danger")); // ajuste conforme a classe real do botão
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", botaoTroca);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botaoTroca);
            System.out.println("[INFO] Clicked submit button.");
            System.out.println("Clique no botão de troca do pedido #" + maiorNumeroPedido);
            modal();
            Thread.sleep(3000);
        } else {
            System.out.println("Nenhum card com status 'Entregue' foi encontrado.");
        }
    }
    void modal(){
        // Aguarda o modal abrir
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalTroca")));

        // Seleciona os livros desejados (exemplo: os 3 disponíveis)
        for (int i = 0; i < 3; i++) {
            String checkboxId = "livroCheck" + i;
            String inputId = "qtdLivro" + i;

            try {
                WebElement checkbox = modal.findElement(By.id(checkboxId));
                checkbox.click(); // ativa o checkbox, o input será habilitado automaticamente via JS

                WebElement inputQtd = modal.findElement(By.id(inputId));
                inputQtd.clear();
                inputQtd.sendKeys("1"); // seleciona 1 unidade
            } catch (NoSuchElementException e) {
                System.out.println("Livro com índice " + i + " não encontrado.");
            }
        }

        // Clica no botão "Enviar Solicitação"
        WebElement botaoEnviar = modal.findElement(By.cssSelector("button.btn.btn-primary"));
        botaoEnviar.click();
    }
    @Test
    @Order(4)
    void testPegarpedido() throws InterruptedException{
        navigateToPage("http://127.0.0.1:5500/historicoCompras.html?id=2");
        System.out.println("[INFO] Page opened successfully.");
        List<WebElement> cards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.card-body")));

        int maiorNumeroPedido = 0;

        for (WebElement card : cards) {
            try {
                // Verifica se o card tem o span com texto "Troca solicitada"
                WebElement span = card.findElement(By.cssSelector("span.badge"));
                if (span.getText().trim().equalsIgnoreCase("Troca solicitada")) {

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

        System.out.println("Maior número de pedido com 'Troca solicitada': #" + maiorNumeroPedido);


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
                if (span.getText().trim().equalsIgnoreCase("Troca solicitada")) {

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
    @Order(2)
    void testTroca() throws InterruptedException {
        int numeroPedido = pegarUltimoPedidoTrocaSolicitado();
        navigateToPage("http://127.0.0.1:5500/pesquisarVendas.html");
        System.out.println("[INFO] Page opened successfully.");

        // Wait for table to be visible
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");

        // Prepare input data
        Map<String, String> inputData = Map.of(
                "numero-pedido", Integer.toString(numeroPedido),
                "status", "TROCA SOLICITADA");

        // Fill form
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        // Click submit button
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-success")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button.");

        // Define locators to be reused for retrying
        By selectLocator = By.cssSelector("select.status-select[data-id='" + numeroPedido + "']");
        By saveButtonLocator = By.cssSelector("button.salvar-status[data-id='" + numeroPedido + "']");

        // Add retry logic for handling stale element
        int maxRetries = 3;
        int attempts = 0;
        boolean success = false;

        while (attempts < maxRetries && !success) {
            try {
                // Wait for the page to stabilize after form submission
                Thread.sleep(1000);

                // Wait for the select element with a fresh reference
                WebElement selectElement = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.refreshed(
                                ExpectedConditions.elementToBeClickable(selectLocator)));

                // Select the option
                new Select(selectElement).selectByValue("TROCA AUTORIZADA");

                // Get fresh reference to save button
                WebElement botaoSalvar = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(saveButtonLocator));

                // Click save using JavaScript for reliability
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", botaoSalvar);
                System.out.println("[INFO] Clicked salvar button.");

                success = true;
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                attempts++;
                System.out.println("[WARNING] Stale element encountered, retrying... (Attempt " + attempts + ")");
                if (attempts >= maxRetries) {
                    throw e; // Re-throw if max retries exceeded
                }
            }
        }

        // Wait for and handle alert
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alert.accept();

        Thread.sleep(3000);
    }
    String pegarUltimoPedidoTrocaAutorizada() {
        navigateToPage("http://127.0.0.1:5500/historicoCompras.html?id=2");
        System.out.println("[INFO] Página de histórico de compras aberta.");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("span.badge"), "Troca autorizada"),
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.card-body"))
        ));
        List<WebElement> cards = driver.findElements(By.cssSelector("div.card-body"));

        System.out.println("[INFO] Total de cards encontrados: " + cards.size());

        int maiorNumeroPedido = 0;
        WebElement cardDoMaiorPedido = null;

        for (WebElement card : cards) {
            try {
                WebElement span = card.findElement(By.cssSelector("span.badge"));
                String status = span.getText().trim();
                System.out.println("[DEBUG] Status do card: " + status);

                if (status.equalsIgnoreCase("Troca autorizada")) {
                    WebElement titulo = card.findElement(By.cssSelector("h5.card-title"));
                    String textoTitulo = titulo.getText(); // Ex: "Pedido #48"
                    System.out.println("[DEBUG] Título do card: " + textoTitulo);

                    Matcher matcher = Pattern.compile("#(\\d+)").matcher(textoTitulo);
                    if (matcher.find()) {
                        int numeroPedido = Integer.parseInt(matcher.group(1));
                        System.out.println("[INFO] Pedido com troca autorizada encontrado: #" + numeroPedido);

                        if (numeroPedido > maiorNumeroPedido) {
                            maiorNumeroPedido = numeroPedido;
                            cardDoMaiorPedido = card;
                            System.out.println("[INFO] Novo maior número de pedido atualizado para: #" + maiorNumeroPedido);
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                System.out.println("[WARN] Elemento esperado não encontrado em um dos cards. Pulando...");
            }
        }

        if (cardDoMaiorPedido != null) {
            try {
                WebElement paragrafoCupom = cardDoMaiorPedido.findElement(By.xpath(".//p[contains(text(), 'Código do cupom:')]"));
                String textoCupom = paragrafoCupom.getText(); // Ex: "Código do cupom: TROCA-117-1276BD"
                System.out.println("[INFO] Texto do cupom encontrado: " + textoCupom);

                Matcher matcher = Pattern.compile("Código do cupom: (\\S+)").matcher(textoCupom);
                if (matcher.find()) {
                    String codigoCupom = matcher.group(1);
                    System.out.println("[INFO] Código do cupom extraído: " + codigoCupom);
                    return codigoCupom;
                } else {
                    System.out.println("[WARN] Não foi possível extrair o código do cupom.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("[WARN] Parágrafo com código do cupom não encontrado no card do pedido #" + maiorNumeroPedido);
            }
        } else {
            System.out.println("[INFO] Nenhum pedido com 'Troca autorizada' foi encontrado.");
        }

        return null; // Se nada for encontrado
    }


    @Test
    @Order(3)
    void testCompraComCupom() throws InterruptedException{
        compra(pegarUltimoPedidoTrocaAutorizada());
    }


    void compra(String cupom) throws InterruptedException{
        // Navegar para a página de compra
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

        // Aplicar cupom
        WebElement inputCupom = wait.until(ExpectedConditions.elementToBeClickable(By.id("input-cupom")));
        inputCupom.sendKeys(cupom); // substitua pelo código real do cupom

        WebElement aplicarCupomBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-aplicar-cupom")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", aplicarCupomBtn);
        System.out.println("[INFO] Cupom aplicado.");
        Thread.sleep(1000); // pequena pausa para aguardar atualização do valor total

        // Localiza o elemento <strong> que contém o valor total
        WebElement totalElemento = driver.findElement(By.cssSelector("#resumo-total strong"));

// Extrai o texto, por exemplo: "Total: R$299,50"
        String totalTexto = totalElemento.getText();

// Remove o "Total: R$" e substitui vírgula por ponto
        String valorNumerico = totalTexto.replace("Total: R$", "").replace(",", ".").trim();

// Localiza o input e define o valor
        inputValor.clear(); // limpa se tiver valor anterior
        inputValor.sendKeys(valorNumerico);
        System.out.println("[INFO] Valor preenchido no cartão.");
        WebElement checarBotaoFinalizarCompra = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finalizarCompraBtn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", checarBotaoFinalizarCompra);
        Thread.sleep(500);  // Pequena pausa para garantir visibilidade
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checarBotaoFinalizarCompra);
        assert checarBotaoFinalizarCompra.getText().equals("Finalizar Compra") : "A botão de finalizar compra não foi localizado.";

        System.out.println("[INFO] Compra finalizada.");

    }
}

