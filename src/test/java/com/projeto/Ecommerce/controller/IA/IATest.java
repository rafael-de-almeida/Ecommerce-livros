package com.projeto.Ecommerce.controller.IA;

import com.projeto.Ecommerce.service.OrdemService;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IATest {
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
    void testPromptIA() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/TelaInicial.html?id=2");

        // Abre o modal do chatbot
        WebElement chatbotButton = wait.until(driver -> driver.findElement(By.className("chat-button")));
        chatbotButton.click();
        System.out.println("[INFO] Chatbot modal opened.");

        // Aguarda o modal carregar
        wait.until(driver -> driver.findElement(By.id("chatModal")).isDisplayed());

        // IDs dos elementos
        WebElement messageBox = driver.findElement(By.id("userMessage"));
        WebElement sendButton = driver.findElement(By.id("sendMessage"));
        WebElement chatbox = driver.findElement(By.id("chatbox"));

        // Lista de prompts
        String[] prompts = {
                "Recomende um livro de aventura",
                "Qual livro é bom para iniciantes em finanças?",
                "Qual é o livro mais vendido na loja?"
        };

        for (String prompt : prompts) {
            messageBox.clear();
            messageBox.sendKeys(prompt);
            sendButton.click();
            System.out.println("[INFO] Enviado: " + prompt);

            // Espera um pouco para o chatbot responder
            Thread.sleep(3000);
        }

        // Verifica se o chatbox contém respostas (pelo menos 3 blocos de texto)
        Assertions.assertTrue(chatbox.getText().split("\n").length >= 3,
                "Chatbot respondeu a todas as mensagens?");
    }


}
