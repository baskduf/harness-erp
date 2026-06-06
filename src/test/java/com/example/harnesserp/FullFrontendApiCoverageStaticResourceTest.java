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
class FullFrontendApiCoverageStaticResourceTest {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void servesAllWorkflowScreensFromTheLegacyShell() throws IOException, InterruptedException {
        HttpResponse<String> response = get("/");

        assertThat(response.statusCode()).isBetween(200, 299);
        assertThat(response.body())
                .contains("Employee Management")
                .contains("Purchase Requests")
                .contains("Approval Queue")
                .contains("Approval History")
                .contains("roleSelector")
                .contains("employeeCreateButton")
                .contains("employeeUpdateButton")
                .contains("purchaseCreateButton")
                .contains("approvalApproveButton")
                .contains("approvalRejectButton")
                .contains("historySearchButton");
    }

    @Test
    void javascriptContainsFullReadmeApiCoverage() throws IOException, InterruptedException {
        HttpResponse<String> script = get("/app.js");

        assertThat(script.statusCode()).isBetween(200, 299);
        assertThat(script.body())
                .contains("headers.set(\"X-ERP-Role\"")
                .contains("apiRequest(\"/employees\"")
                .contains("method: \"POST\"")
                .contains("method: \"PUT\"")
                .contains("name: requestedName")
                .contains("/employees/")
                .contains("apiRequest(\"/purchase-requests\"")
                .contains("employeeId: filters.employeeId")
                .contains("query.employeeId")
                .contains("query.status")
                .contains("/purchase-requests/")
                .contains("approve")
                .contains("reject")
                .contains("/approvals")
                .contains("loadApprovalHistory")
                .contains("loadApprovalQueue");
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
