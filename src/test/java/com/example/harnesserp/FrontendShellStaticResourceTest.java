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
class FrontendShellStaticResourceTest {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void servesFrontendShellAtRoot() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/");

        assertThat(response.statusCode()).isBetween(200, 299);
        assertThat(response.body())
                .contains("HARNESS ERP - Operations Workspace [Local]")
                .contains("Employee Management")
                .contains("Purchase Requests")
                .contains("Approval Queue")
                .contains("Approval History")
                .contains("Role Policy Reference")
                .contains("roleSelector")
                .contains("erp-status-bar")
                .contains("/styles.css")
                .contains("/app.js");
    }

    @Test
    void servesSharedFrontendAssets() throws IOException, InterruptedException {
        HttpResponse<String> styles = get("/styles.css");
        HttpResponse<String> script = get("/app.js");

        assertThat(styles.statusCode()).isBetween(200, 299);
        assertThat(styles.body())
                .contains("--erp-titlebar")
                .contains(".erp-status-bar")
                .contains(".erp-grid");

        assertThat(script.statusCode()).isBetween(200, 299);
        assertThat(script.body())
                .contains("apiRequest")
                .contains("setStatus")
                .contains("formatAmount")
                .contains("X-ERP-Role");
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
