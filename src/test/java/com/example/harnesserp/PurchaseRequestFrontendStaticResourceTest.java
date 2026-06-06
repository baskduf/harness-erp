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
class PurchaseRequestFrontendStaticResourceTest {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void servesPurchaseRequestControls() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/");

        assertThat(response.statusCode()).isBetween(200, 299);
        assertThat(response.body())
                .contains("purchaseFilterEmployeeId")
                .contains("purchaseFilterStatus")
                .contains("purchaseRows")
                .contains("purchaseEmployeeId")
                .contains("purchaseEmployeeLookup")
                .contains("purchaseDescription")
                .contains("purchaseAmount")
                .contains("purchaseCreateButton")
                .contains("erp-number-field");
    }

    @Test
    void servesPurchaseRequestApiJavascriptWiring() throws IOException, InterruptedException {
        HttpResponse<String> script = get("/app.js");

        assertThat(script.statusCode()).isBetween(200, 299);
        assertThat(script.body())
                .contains("apiRequest(\"/purchase-requests\"")
                .contains("/purchase-requests/")
                .contains("apiRequest(\"/employees\")")
                .contains("employeeId")
                .contains("status")
                .contains("method: \"POST\"")
                .contains("amount: amount")
                .contains("X-ERP-Role");
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
