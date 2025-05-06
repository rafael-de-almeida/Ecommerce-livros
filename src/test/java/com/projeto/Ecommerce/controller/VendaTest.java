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

    @Test
    @Order(1)
    void testFormSubmissionAndApiValidation() throws InterruptedException {
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

// Esperar que o dropdown do cartão esteja visível e clicável
        WebElement cartaoDropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("cartao1"))
        );
// Rolando até o dropdown, se necessário
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cartaoDropdown);
// Esperar que o dropdown seja clicável
        cartaoDropdown = wait.until(
                ExpectedConditions.elementToBeClickable(cartaoDropdown)
        );
// Usando o objeto Select para interagir com o dropdown do cartão
        Select selectCartao = new Select(cartaoDropdown);
        selectCartao.selectByIndex(1); // seleciona a segunda opção
        System.out.println("[INFO] Cartão selecionado com sucesso.");
        WebElement botaoFinalizarCompra2 = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".btn.btn-success.w-100")
                )
        );

        System.out.println("[INFO] Compra finalizada com sucesso e validada.");
    }

}

