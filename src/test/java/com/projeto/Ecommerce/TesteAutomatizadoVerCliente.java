package com.projeto.Ecommerce;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// a fazer
// 1° fazer o pesquisar clientes, pegando o id mais velho já feito
// deletar esse id e editar

    @SpringBootTest
    class TesteAutomatizadoVerCliente {

        private WebDriver driver;
        private WebDriverWait wait;

        @BeforeEach
        void setup() {
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get("http://127.0.0.1:5500/pesquisarUsuario.html");

            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            System.out.println("[INFO] Opened the page successfully.");
        }

        @AfterEach
        void tearDown() {
            driver.quit();
            System.out.println("[INFO] Browser closed.");
        }


        @Test
        void testClientDataInTable() throws InterruptedException {
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

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));

            // Scroll to button
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
            Thread.sleep(500);

            // Click using JavaScript to bypass overlays
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
            System.out.println("[INFO] Clicked submit button.");

            // Wait for table update (optional: adjust the wait time)
            Thread.sleep(4000);

            // Get all table rows
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            boolean isClientFound = false;

            // Loop through rows and check if expected values are present
            for (WebElement row : rows) {
                List<WebElement> columns = row.findElements(By.tagName("td"));

                if (columns.size() >= inputData.size()) { // Ensure row has enough columns
                    // Extract data from columns and create a HashMap
                    Map<String, String> rowData = new HashMap<>();
                    rowData.put("nome", columns.get(0).getText());
                    rowData.put("genero", columns.get(1).getText());//genero
                    rowData.put("data-nascimento", columns.get(2).getText());//dt nasc
                    rowData.put("idade", columns.get(3).getText());//idade
                    rowData.put("cpf", columns.get(4).getText());
                    rowData.put("email", columns.get(5).getText());//email
                    rowData.put("telefone", columns.get(6).getText());//telefone

                    // Debug: Print row data
                    System.out.println("[DEBUG] Checking row: " + rowData);

                    // Compare row data with expected data
                    if (inputData.equals(rowData)) {
                        isClientFound = true;
                        break; // Stop searching if we find a match
                    }
                }
            }

            // ✅ Assert only after checking all rows
            assert isClientFound : "[ERROR] Client not found in the table!";
            System.out.println("[INFO] Client found in the table successfully.");
        }

        private void preencherInput(String name, String value) {
            WebElement inputField = driver.findElement(By.name(name));
            inputField.sendKeys(value);
            System.out.println("[INFO] Filled input '" + name + "' with '" + value + "'.");
        }
    }


