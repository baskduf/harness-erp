package com.example.harnesserp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeFrontendStaticResourceTest {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void servesEmployeeManagementControls() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/");

        assertThat(response.statusCode()).isBetween(200, 299);
        assertThat(response.body())
                .contains("employeeSearchName")
                .contains("employeeRows")
                .contains("employeeDetailId")
                .contains("employeeName")
                .contains("employeeDepartment")
                .contains("employeeCreateButton")
                .contains("employeeUpdateButton")
                .contains("erp-required-field");
    }

    @Test
    void servesEmployeeApiJavascriptWiring() throws IOException, InterruptedException {
        HttpResponse<String> script = get("/app.js");
        HttpResponse<String> styles = get("/styles.css");

        assertThat(script.statusCode()).isBetween(200, 299);
        assertThat(script.body())
                .contains("apiRequest(\"/employees\"")
                .contains("apiRequest(\"/employees/\"")
                .contains("method: \"POST\"")
                .contains("method: \"PUT\"")
                .contains("X-ERP-Role")
                .contains("Required value is missing. [Name]")
                .contains("Required value is missing. [Department]");

        assertThat(styles.statusCode()).isBetween(200, 299);
        assertThat(styles.body())
                .contains(".erp-required-field")
                .contains("--erp-input-required")
                .contains("input[readonly]");
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
