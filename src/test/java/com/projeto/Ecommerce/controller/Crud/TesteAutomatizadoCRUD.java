package com.projeto.Ecommerce.controller.Crud;

import com.projeto.Ecommerce.service.ClientesService;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TesteAutomatizadoCRUD {
    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private ClientesService cliente;

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

    // NOVO TESTE: Validação de senhas que não coincidem
    @Test
    @Order(1)
    void testRegistrationWithMismatchedPasswords() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/CadastroCliente.html");

        // Captura o ID do último cliente para verificar que nenhum novo será criado
        Integer initialLastClientId = cliente.getLastClient();
        System.out.println("[INFO] Initial last client ID: " + initialLastClientId);

        // Preenche o formulário com senhas diferentes
        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "Teste Senha Diferente");
        inputData.put("genero", "Outro");
        inputData.put("data_de_nascimento", "2000-01-01");
        inputData.put("senha", "SenhaForte123@");
        inputData.put("confirmar_senha", "SenhaDIFERENTE123@"); // Senha diferente
        inputData.put("cpf", "99988877766");
        inputData.put("email", "senha.diferente@teste.com");
        inputData.put("telefone", "11999998888");
        inputData.put("idade", "24");

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button with mismatched passwords.");

        // Valida o alerta de erro
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alert.accept();

        // Verifica se a mensagem de erro esperada foi exibida (ajuste a string se necessário)
        assertTrue(alertText.toLowerCase().contains("senhas não coincidem"), "A mensagem de erro de senhas divergentes não apareceu. Mensagem exibida: " + alertText);

        // Verifica se nenhum cliente novo foi criado
        Integer finalLastClientId = cliente.getLastClient();
        assertEquals(initialLastClientId, finalLastClientId, "[ERROR] Um novo cliente foi criado mesmo com senhas divergentes.");
        System.out.println("[SUCCESS] Teste de senhas divergentes passou. Nenhum cliente foi criado.");
    }

    // NOVO TESTE: Validação de senha fraca
    @Test
    @Order(2)
    void testRegistrationWithWeakPassword() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/CadastroCliente.html");

        // Captura o ID do último cliente para verificar que nenhum novo será criado
        Integer initialLastClientId = cliente.getLastClient();
        System.out.println("[INFO] Initial last client ID: " + initialLastClientId);

        // Preenche o formulário com uma senha fraca
        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "Teste Senha Fraca");
        inputData.put("genero", "Outro");
        inputData.put("data_de_nascimento", "2000-01-02");
        inputData.put("senha", "123"); // Senha fraca
        inputData.put("confirmar_senha", "123"); // Senha fraca
        inputData.put("cpf", "99988877755");
        inputData.put("email", "senha.fraca@teste.com");
        inputData.put("telefone", "11999997777");
        inputData.put("idade", "24");

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button with a weak password.");

        // Valida o alerta de erro
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alert.accept();

        // Verifica se a mensagem de erro esperada foi exibida (ajuste a string se necessário)
        assertTrue(alertText.toLowerCase().contains("requisitos de segurança"), "A mensagem de erro de senha fraca não apareceu. Mensagem exibida: " + alertText);
        // Verifica se nenhum cliente novo foi criado
        Integer finalLastClientId = cliente.getLastClient();
        assertEquals(initialLastClientId, finalLastClientId, "[ERROR] Um novo cliente foi criado mesmo com senha fraca.");
        System.out.println("[SUCCESS] Teste de senha fraca passou. Nenhum cliente foi criado.");
    }


    @Test
    @Order(3) // Antigo Order(1)
    void testFormSubmissionAndApiValidation() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/CadastroCliente.html");


        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");


        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("genero", "Masculino");
        inputData.put("data_de_nascimento", "2004-07-22");
        inputData.put("senha", "123joao@");
        inputData.put("confirmar_senha", "123joao@");
        inputData.put("cpf", "123123123");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20");


        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));


        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button.");


        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alert.accept();


        boolean isSuccess = alertText.contains("success") || alertText.contains("Cliente cadastrado com sucesso!");
        assertTrue(isSuccess, "[ERROR] Form submission failed: " + alertText);

        if (isSuccess) {
            validateClientData(inputData);
        }
    }

    @Test
    @Order(4) // Antigo Order(2)
    void testClientDataInTable() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/pesquisarUsuario.html");

        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");


        Map<String, String> inputData = Map.of("nome", "João Silva", "email", "rafael@gmail.com", "genero", "Masculino", "data-nascimento", "2004-07-22", "cpf", "123123123", "telefone", "11987654321", "idade", "20");


        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));


        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button.");

        Thread.sleep(4000);


        List<WebElement> rows = table.findElements(By.tagName("tr"));
        boolean isClientFound = false;

        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (columns.size() >= inputData.size()) {
                Map<String, String> rowData = Map.of("nome", columns.get(0).getText(), "genero", columns.get(1).getText(), "data-nascimento", columns.get(2).getText(), "idade", columns.get(3).getText(), "cpf", columns.get(4).getText(), "email", columns.get(5).getText(), "telefone", columns.get(6).getText());

                System.out.println("[DEBUG] Checking row: " + rowData);

                // Compara se todos os valores da linha correspondem aos dados de entrada
                boolean allMatch = inputData.entrySet().stream()
                        .allMatch(entry -> Objects.equals(entry.getValue(), rowData.get(entry.getKey())));

                if (allMatch) {
                    isClientFound = true;
                    break;
                }
            }
        }

        assertTrue(isClientFound, "[ERROR] Client not found in the table!");
        System.out.println("[INFO] Client found in the table successfully.");
    }

    private void validateClientData(Map<String, String> inputData) {
        Integer lastClientId = cliente.getLastClient();
        assertNotNull(lastClientId, "[ERROR] No client found with the expected ID!");
        System.out.println("[INFO] Found client with ID: " + lastClientId);

        Response response = given().baseUri("http://localhost:8080/site").when().get("/clientes/get").then().statusCode(200).extract().response();

        System.out.println("[INFO] API response: " + response.getBody().asString());

        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);

        assertNotNull(clientData, "[ERROR] No client data found in the API response!");

        Map<String, String> fieldMapping = Map.of("nome", "CLI_NOME", "genero", "CLI_GENERO", "data_de_nascimento", "CLI_NASCIMENTO", "cpf", "CLI_CPF", "email", "CLI_EMAIL", "telefone", "CLI_TELEFONE", "idade", "CLI_IDADE");

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            String formField = entry.getKey();
            String expectedValue = entry.getValue();

            if (formField.equals("senha") || formField.equals("confirmar_senha")) {
                continue;
            }

            String jsonField = fieldMapping.getOrDefault(formField, formField);
            Object actualValueObj = clientData.get(jsonField);
            assertNotNull(actualValueObj, "[ERROR] Field '" + jsonField + "' is missing in the API response!");

            String actualValue = actualValueObj.toString();
            assertEquals(expectedValue, actualValue, "[ERROR] Mismatch for field '" + formField + "'. Expected: '" + expectedValue + "', Got: '" + actualValue + "'");
        }

        System.out.println("[INFO] All client data matches the API response.");
    }

    @Order(5) // Antigo Order(3)
    @Test
    void testFormEditingAndApiValidation() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/pesquisarUsuario.html");


        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");


        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("genero", "Masculino");
        inputData.put("data-nascimento", "2004-07-22");
        inputData.put("cpf", "123123123");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20");


        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }


        Integer lastClientId = cliente.getLastClient();
        assertNotNull(lastClientId, "[ERROR] No client found with the expected ID!");
        System.out.println("[INFO] Found client with ID: " + lastClientId);


        Response response = given().baseUri("http://localhost:8080/site").when().get("/clientes/get")
                .then().statusCode(200).extract().response();


        System.out.println("[INFO] API response: " + response.getBody().asString());

        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);


        System.out.println("[DEBUG] Retrieved client data: " + clientData);
        assertNotNull(clientData, "[ERROR] No client data found in the API response!");


        WebElement formButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", formButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", formButton);
        System.out.println("[INFO] Clicked form button.");


        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@onclick, 'editarCliente(" + lastClientId + ")')]")));


        WebElement tableContainer = driver.findElement(By.id("dataTable"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tableContainer);
        Thread.sleep(500);


        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick, 'editarCliente(" + lastClientId + ")')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editButton);
        System.out.println("[INFO] Clicked the Edit button successfully.");


        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] New page loaded successfully.");


        String expectedUrl = "http://127.0.0.1:5500/AlterarDados.html?id=" + lastClientId;
        wait.until(ExpectedConditions.urlToBe(expectedUrl));
        System.out.println("[INFO] Redirected to the expected page: " + driver.getCurrentUrl());

        editFormTest();
    }

    private void editFormTest() {
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");

        Integer lastClientId = cliente.getLastClient();
        assertNotNull(lastClientId, "[ERROR] No client found with the expected ID!");
        System.out.println("[INFO] Found client with ID: " + lastClientId);

        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("genero", "Masculino");
        inputData.put("data-nascimento", "2004-07-22");
        inputData.put("cpf", "123123123");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20");


        Map<String, String> apiFieldMapping = new HashMap<>();
        apiFieldMapping.put("nome", "CLI_NOME");
        apiFieldMapping.put("email", "CLI_EMAIL");
        apiFieldMapping.put("genero", "CLI_GENERO");
        apiFieldMapping.put("data-nascimento", "CLI_NASCIMENTO");
        apiFieldMapping.put("cpf", "CLI_CPF");
        apiFieldMapping.put("telefone", "CLI_TELEFONE");
        apiFieldMapping.put("idade", "CLI_IDADE");


        inputData.forEach((key, value) -> {
            limparInput(key);
            preencherInput(key, value);
        });


        WebElement formButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("formbold-btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", formButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", formButton);
        System.out.println("[INFO] Clicked form button.");


        Response response = given().baseUri("http://localhost:8080/site").when().get("/clientes/get")
                .then().statusCode(200).extract().response();

        System.out.println("[INFO] API response: " + response.getBody().asString());


        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);
        assertNotNull(clientData, "[ERROR] No client data found in the API response!");


        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            String formField = entry.getKey();
            String expectedValue = entry.getValue();

            String apiField = apiFieldMapping.get(formField);
            Object apiValue = clientData.get(apiField);

            if ("idade".equals(formField) && apiValue != null) {
                Integer idadeValue = Integer.parseInt(expectedValue);
                assertEquals(idadeValue, apiValue, "[ERROR] Mismatch in " + formField);
                System.out.println("[INFO] " + formField + " matches: " + idadeValue);

            } else {
                assertNotNull(apiValue, "[ERROR] Mismatch in " + formField + ": API value is null");
                assertEquals(expectedValue, apiValue.toString(), "[ERROR] Mismatch in " + formField);
                System.out.println("[INFO] " + formField + " matches: " + expectedValue);
            }
        }
    }

    @Order(6) // Antigo Order(4)
    @Test
    void testDel() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/pesquisarUsuario.html");


        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");

        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("genero", "Masculino");
        inputData.put("data-nascimento", "2004-07-22");
        inputData.put("cpf", "123123123");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20");


        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }


        Integer lastClientId = cliente.getLastClient();
        assertNotNull(lastClientId, "[ERROR] No client found with the expected ID!");
        System.out.println("[INFO] Found client with ID: " + lastClientId);


        Response response = given().baseUri("http://localhost:8080/site").when().get("/clientes/get").then().statusCode(200).extract().response();

        System.out.println("[INFO] API response: " + response.getBody().asString());


        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);

        System.out.println("[DEBUG] Retrieved client data: " + clientData);
        assertNotNull(clientData, "[ERROR] No client data found in the API response!");

        WebElement formButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", formButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", formButton);
        System.out.println("[INFO] Clicked form button.");


        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@onclick, 'excluirCliente(" + lastClientId + ")')]")));


        WebElement tableContainer = driver.findElement(By.id("dataTable"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tableContainer);
        Thread.sleep(500);


        WebElement delButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@onclick, 'excluirCliente(" + lastClientId + ")')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", delButton);
        System.out.println("[INFO] Clicked the Del button successfully.");

        Alert alertConfirm = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertConfirm.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alertConfirm.accept();
        Alert alertDel = wait.until(ExpectedConditions.alertIsPresent());
        alertText = alertDel.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);

        boolean isSuccess = alertText.contains("Cliente excluído com sucesso!");
        assertTrue(isSuccess, "[ERROR] Form submission failed: " + alertText);
    }

    private void preencherInput(String name, String value) {
        WebElement inputField = driver.findElement(By.name(name));
        String type = inputField.getAttribute("type");

        if ("date".equalsIgnoreCase(type)) {

            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", inputField, value);
            System.out.println("[INFO] Campo de data '" + name + "' preenchido com JavaScript: " + value);
        } else {
            inputField.sendKeys(value);
            System.out.println("[INFO] Filled input '" + name + "' with '" + value + "'.");
        }
    }


    private void limparInput(String name) {
        WebElement inputField = driver.findElement(By.name(name));
        inputField.clear();
        System.out.println("[INFO] Cleaned input " + name);
    }

}