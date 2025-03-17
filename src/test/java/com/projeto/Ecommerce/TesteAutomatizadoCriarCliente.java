package com.projeto.Ecommerce;
// a fazer
// 1° fazer o pesquisar clientes, pegando o id mais velho já feito
// deletar esse id e editar

import com.projeto.Ecommerce.service.ClientesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import io.restassured.response.Response;


import java.time.Duration;
import java.util.HashMap;

import java.util.Map;

import static io.restassured.RestAssured.given;


@SpringBootTest
class TesteAutomatizadoCriarCliente {

	private WebDriver driver;
	private WebDriverWait wait;

	@Autowired
	private ClientesService cliente;

	@BeforeEach
	void setup() {
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("http://127.0.0.1:5500/CadastroCliente.html");

		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		System.out.println("[INFO] Opened the page successfully.");
	}

	@AfterEach
	void tearDown() {
		driver.quit();
		System.out.println("[INFO] Browser closed.");
	}


	@Test
	void testFormSubmissionAndApiValidation() throws InterruptedException {
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

		// Scroll to button
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
		Thread.sleep(500);

		// Click using JavaScript to bypass overlays
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
		System.out.println("[INFO] Clicked submit button.");

		// Wait for alert
		Alert alert = wait.until(ExpectedConditions.alertIsPresent());
		String alertText = alert.getText();
		System.out.println("[INFO] Alert displayed: " + alertText);
		alert.accept();

		// Success check
		boolean isSuccess = alertText.contains("success") || alertText.contains("Client successfully posted!");
		assert isSuccess : "[ERROR] Form submission failed: " + alertText;

		if (isSuccess) {
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

			// Field name mappings between frontend & backend
			Map<String, String> fieldMapping = Map.of(
					"nome", "CLI_NOME",
					"genero", "CLI_GENERO",
					"data_de_nascimento", "CLI_NASCIMENTO",
					"cpf", "CLI_CPF",
					"email", "CLI_EMAIL",
					"telefone", "CLI_TELEFONE",
					"idade", "CLI_IDADE"
			);

			// Validate each field, skipping ones not stored in the API
			for (Map.Entry<String, String> entry : inputData.entrySet()) {
				String formField = entry.getKey();
				String expectedValue = entry.getValue();

				// Ignore 'confirmar_senha' as it is not stored in the database
				if (formField.equals("senha") || formField.equals("confirmar_senha")) {
					continue;
				}

				// Get the corresponding backend field name
				String jsonField = fieldMapping.getOrDefault(formField, formField);

				// Check if the field exists in API response
				Object actualValueObj = clientData.get(jsonField);
				assert actualValueObj != null : String.format("[ERROR] Field '%s' is missing in the API response!", jsonField);

				// Convert to string and compare values
				String actualValue = actualValueObj.toString();
				assert expectedValue.equals(actualValue) : String.format(
						"[ERROR] Mismatch for field '%s'. Expected: '%s', Got: '%s'",
						formField, expectedValue, actualValue);
			}

			System.out.println("[INFO] All client data matches the API response.");
		}
	}

	private void preencherInput(String name, String value) {
		WebElement inputField = driver.findElement(By.name(name));
		inputField.sendKeys(value);
		System.out.println("[INFO] Filled input '" + name + "' with '" + value + "'.");
	}

}

