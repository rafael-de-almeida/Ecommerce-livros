package com.projeto.Ecommerce;

import com.projeto.Ecommerce.service.ClientesService;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static io.restassured.RestAssured.given;

@SpringBootTest
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

    @Test
    void testFormSubmissionAndApiValidation() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/CadastroCliente.html");

        // Locate form
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");

        // Input data
        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("genero", "Masculino");
        inputData.put("data_de_nascimento", "2004-07-22");
        inputData.put("senha", "123");
        inputData.put("confirmar_senha", "123");
        inputData.put("cpf", "123123123");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20");

        // Fill form dynamically
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("formbold-btn")));

        // Scroll to button and click
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button.");

        // Handle alert
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alert.accept();

        // Check success
        boolean isSuccess = alertText.contains("success") || alertText.contains("Client successfully posted!");
        assert isSuccess : "[ERROR] Form submission failed: " + alertText;

        if (isSuccess) {
            validateClientData(inputData);
        }
    }

    @Test
    void testClientDataInTable() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/pesquisarUsuario.html");

        // Locate elements
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");

        // Input data
        Map<String, String> inputData = Map.of(
                "nome", "João Silva",
                "email", "rafael@gmail.com",
                "genero", "Masculino",
                "data-nascimento", "2004-07-22",
                "cpf", "123123123",
                "telefone", "11987654321",
                "idade", "20"
        );

        // Fill form
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));

        // Scroll to button and click
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
        System.out.println("[INFO] Clicked submit button.");

        // Wait for table update
        Thread.sleep(4000);

        // Verify table data
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        boolean isClientFound = false;

        for (WebElement row : rows) {
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (columns.size() >= inputData.size()) {
                Map<String, String> rowData = Map.of(
                        "nome", columns.get(0).getText(),
                        "genero", columns.get(1).getText(),
                        "data-nascimento", columns.get(2).getText(),
                        "idade", columns.get(3).getText(),
                        "cpf", columns.get(4).getText(),
                        "email", columns.get(5).getText(),
                        "telefone", columns.get(6).getText()
                );

                System.out.println("[DEBUG] Checking row: " + rowData);
                if (inputData.equals(rowData)) {
                    isClientFound = true;
                    break;
                }
            }
        }

        assert isClientFound : "[ERROR] Client not found in the table!";
        System.out.println("[INFO] Client found in the table successfully.");
    }

    private void validateClientData(Map<String, String> inputData) {
        Integer lastClientId = cliente.getLastClient();
        assert lastClientId != null : "[ERROR] No client found with the expected ID!";
        System.out.println("[INFO] Found client with ID: " + lastClientId);

        Response response = given()
                .baseUri("http://localhost:8080/site")
                .when()
                .get("/clientes/get")
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("[INFO] API response: " + response.getBody().asString());

        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);

        assert clientData != null : "[ERROR] No client data found in the API response!";

        Map<String, String> fieldMapping = Map.of(
                "nome", "CLI_NOME",
                "genero", "CLI_GENERO",
                "data_de_nascimento", "CLI_NASCIMENTO",
                "cpf", "CLI_CPF",
                "email", "CLI_EMAIL",
                "telefone", "CLI_TELEFONE",
                "idade", "CLI_IDADE"
        );

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            String formField = entry.getKey();
            String expectedValue = entry.getValue();

            if (formField.equals("senha") || formField.equals("confirmar_senha")) {
                continue;
            }

            String jsonField = fieldMapping.getOrDefault(formField, formField);
            Object actualValueObj = clientData.get(jsonField);
            assert actualValueObj != null : "[ERROR] Field '" + jsonField + "' is missing in the API response!";

            String actualValue = actualValueObj.toString();
            assert expectedValue.equals(actualValue) : "[ERROR] Mismatch for field '" + formField + "'. Expected: '" + expectedValue + "', Got: '" + actualValue + "'";
        }

        System.out.println("[INFO] All client data matches the API response.");
    }
    @Test
    void testFormEditingAndApiValidation() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/pesquisarUsuario.html");

        // Locate form
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");

        // Input data
        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("genero", "Masculino");
        inputData.put("data-nascimento", "2004-07-22");
        inputData.put("cpf", "123123123");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20");

        // Fill form dynamically
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        // Get the last client ID
        Integer lastClientId = cliente.getLastClient();
        assert lastClientId != null : "[ERROR] No client found with the expected ID!";
        System.out.println("[INFO] Found client with ID: " + lastClientId);

        // Make API request
        Response response = given()
                .baseUri("http://localhost:8080/site")
                .when()
                .get("/clientes/get")  // Replace with the correct endpoint
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Print API response for debugging
        System.out.println("[INFO] API response: " + response.getBody().asString());

        // JSONPath query to get the client data
        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);

        // Debug: Print client data from API
        System.out.println("[DEBUG] Retrieved client data: " + clientData);
        assert clientData != null : "[ERROR] No client data found in the API response!";

        // Click form submission button
        WebElement formButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", formButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", formButton);
        System.out.println("[INFO] Clicked form button.");

        // Wait for Edit button to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@onclick, 'editarCliente(" + lastClientId + ")')]")
        ));

        // Scroll to table container
        WebElement tableContainer = driver.findElement(By.id("dataTable")); // Change to your actual container ID
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tableContainer);
        Thread.sleep(500);

        // Wait for Edit button to be clickable and click
        WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@onclick, 'editarCliente(" + lastClientId + ")')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editButton);
        System.out.println("[INFO] Clicked the Edit button successfully.");

        // ✅ **Wait for the new page to load**
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] New page loaded successfully.");

        // ✅ **Verify the URL change**
        String expectedUrl = "http://127.0.0.1:5500/AlterarDados.html?id=" + lastClientId; // Change this to the correct expected URL
        wait.until(ExpectedConditions.urlToBe(expectedUrl));
        System.out.println("[INFO] Redirected to the expected page: " + driver.getCurrentUrl());

        editFormTest();
    }
    private void editFormTest() {
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");

        Integer lastClientId = cliente.getLastClient();
        assert lastClientId != null : "[ERROR] No client found with the expected ID!";
        System.out.println("[INFO] Found client with ID: " + lastClientId);

        // Input data map (form fields)
        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("genero", "Masculino");
        inputData.put("data-nascimento", "2004-07-22");
        inputData.put("cpf", "123123123");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20"); // String because it's coming from a form

        // Mapping form field names to API field names
        Map<String, String> apiFieldMapping = new HashMap<>();
        apiFieldMapping.put("nome", "CLI_NOME");
        apiFieldMapping.put("email", "CLI_EMAIL");
        apiFieldMapping.put("genero", "CLI_GENERO");
        apiFieldMapping.put("data-nascimento", "CLI_NASCIMENTO"); // No "DATA_" prefix
        apiFieldMapping.put("cpf", "CLI_CPF");
        apiFieldMapping.put("telefone", "CLI_TELEFONE");
        apiFieldMapping.put("idade", "CLI_IDADE");

        // Clear and refill form with input data
        inputData.forEach((key, value) -> {
            limparInput(key);
            preencherInput(key, value);
        });

        // Submit form
        WebElement formButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("formbold-btn")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", formButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", formButton);
        System.out.println("[INFO] Clicked form button.");

        // Wait for API response
        Response response = given()
                .baseUri("http://localhost:8080/site")
                .when()
                .get("/clientes/get")  // Replace with the correct endpoint
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("[INFO] API response: " + response.getBody().asString());

        // Retrieve client data from API response
        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);
        assert clientData != null : "[ERROR] No client data found in the API response!";

        // Compare form data with API response
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            String formField = entry.getKey();
            String expectedValue = entry.getValue();

            // Get the corresponding API field name
            String apiField = apiFieldMapping.get(formField);
            Object apiValue = clientData.get(apiField);

            // Handle integer fields
            if ("idade".equals(formField) && apiValue != null) {
                Integer idadeValue = Integer.parseInt(expectedValue); // Convert to Integer for comparison
                if (!idadeValue.equals(apiValue)) {
                    System.out.println("[ERROR] Mismatch in " + formField + ": Form value = " + idadeValue + ", API value = " + apiValue);
                    assert false : "[ERROR] Mismatch in " + formField;
                } else {
                    System.out.println("[INFO] " + formField + " matches: " + idadeValue);
                }
            } else if (apiValue == null || !expectedValue.equals(apiValue.toString())) {
                System.out.println("[ERROR] Mismatch in " + formField + ": Form value = " + expectedValue + ", API value = " + apiValue);
                assert false : "[ERROR] Mismatch in " + formField;
            } else {
                System.out.println("[INFO] " + formField + " matches: " + expectedValue);
            }
        }
    }
    @Test
    void testDel() throws InterruptedException {
        navigateToPage("http://127.0.0.1:5500/pesquisarUsuario.html");

        // Locate form
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form-cliente")));
        System.out.println("[INFO] Form found successfully.");

        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dataTable")));
        System.out.println("[INFO] Table found successfully.");

        // Input data
        Map<String, String> inputData = new HashMap<>();
        inputData.put("nome", "João Silva");
        inputData.put("email", "rafael@gmail.com");
        inputData.put("genero", "Masculino");
        inputData.put("data-nascimento", "2004-07-22");
        inputData.put("cpf", "123123123");
        inputData.put("telefone", "11987654321");
        inputData.put("idade", "20");

        // Fill form dynamically
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            preencherInput(entry.getKey(), entry.getValue());
        }

        // Get the last client ID
        Integer lastClientId = cliente.getLastClient();
        assert lastClientId != null : "[ERROR] No client found with the expected ID!";
        System.out.println("[INFO] Found client with ID: " + lastClientId);

        // Make API request
        Response response = given()
                .baseUri("http://localhost:8080/site")
                .when()
                .get("/clientes/get")  // Replace with the correct endpoint
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Print API response for debugging
        System.out.println("[INFO] API response: " + response.getBody().asString());

        // JSONPath query to get the client data
        String jsonPathQuery = String.format("find { it.cliId == %s }", lastClientId);
        Map<String, Object> clientData = response.jsonPath().getMap(jsonPathQuery);

        // Debug: Print client data from API
        System.out.println("[DEBUG] Retrieved client data: " + clientData);
        assert clientData != null : "[ERROR] No client data found in the API response!";

        // Click form submission button
        WebElement formButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", formButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", formButton);
        System.out.println("[INFO] Clicked form button.");

        // Wait for Edit button to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(@onclick, 'excluirCliente(" + lastClientId + ")')]")
        ));

        // Scroll to table container
        WebElement tableContainer = driver.findElement(By.id("dataTable")); // Change to your actual container ID
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", tableContainer);
        Thread.sleep(500);

        // Wait for Edit button to be clickable and click
        WebElement delButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@onclick, 'excluirCliente(" + lastClientId + ")')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", delButton);
        System.out.println("[INFO] Clicked the Del button successfully.");

        Alert alertConfirm = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alertConfirm.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        alertConfirm.accept();
        Alert alertDel = wait.until(ExpectedConditions.alertIsPresent());
        alertText = alertDel.getText();
        System.out.println("[INFO] Alert displayed: " + alertText);
        // Success check
        boolean isSuccess = alertText.contains("Cliente excluído com sucesso!");
        assert isSuccess : "[ERROR] Form submission failed: " + alertText;
    }
    private void preencherInput(String name, String value) {
        WebElement inputField = driver.findElement(By.name(name));
        inputField.sendKeys(value);
        System.out.println("[INFO] Filled input '" + name + "' with '" + value + "'.");
    }
    private void limparInput(String name) {
        WebElement inputField = driver.findElement(By.name(name));
        inputField.clear();
        System.out.println("[INFO] Cleaned input " + name);
    }

}

