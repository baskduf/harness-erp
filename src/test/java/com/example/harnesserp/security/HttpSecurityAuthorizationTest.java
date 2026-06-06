package com.example.harnesserp.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpSecurityAuthorizationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void readEndpointsAndStaticResourcesRemainPublic() throws IOException, InterruptedException {
        assertThat(get("/").statusCode()).isBetween(200, 299);
        assertThat(get("/employees").statusCode()).isBetween(200, 299);
        assertThat(get("/purchase-requests").statusCode()).isBetween(200, 299);
    }

    @Test
    void employeeMutationsRequireAdminAtHttpLayer() throws IOException, InterruptedException {
        String body = employeeBody("Security Admin Target", "Finance");

        assertForbidden(post("/employees", body, null), "ADMIN role is required to create employees");
        assertForbidden(post("/employees", body, "EMPLOYEE"), "ADMIN role is required to create employees");

        JsonNode created = json(post("/employees", body, "ADMIN"));
        long employeeId = created.get("id").asLong();

        String updateBody = employeeBody("Security Admin Updated", "Operations");
        assertForbidden(
                put("/employees/" + employeeId, updateBody, "MANAGER"),
                "ADMIN role is required to update employees"
        );

        HttpResponse<String> updated = put("/employees/" + employeeId, updateBody, "ADMIN");
        assertThat(updated.statusCode()).isEqualTo(200);
        assertThat(updated.body()).contains("Security Admin Updated", "Operations");
    }

    @Test
    void purchaseRequestCreationRequiresEmployeeAtHttpLayer() throws IOException, InterruptedException {
        long employeeId = createEmployee("Purchase Security Employee", "Purchasing");
        String body = purchaseRequestBody(employeeId, "Docking station", new BigDecimal("125.50"), "SUBMITTED");

        assertForbidden(
                post("/purchase-requests", body, null),
                "EMPLOYEE role is required to create purchase requests"
        );
        assertForbidden(
                post("/purchase-requests", body, "ADMIN"),
                "EMPLOYEE role is required to create purchase requests"
        );

        HttpResponse<String> created = post("/purchase-requests", body, "EMPLOYEE");
        assertThat(created.statusCode()).isEqualTo(201);
        assertThat(created.body()).contains("Docking station", "SUBMITTED");
    }

    @Test
    void approvalDecisionsRequireManagerAtHttpLayer() throws IOException, InterruptedException {
        long employeeId = createEmployee("Approval Security Employee", "Operations");
        JsonNode purchaseRequest = json(post(
                "/purchase-requests",
                purchaseRequestBody(employeeId, "Replacement monitor", new BigDecimal("410.00"), "SUBMITTED"),
                "EMPLOYEE"
        ));
        long purchaseRequestId = purchaseRequest.get("id").asLong();
        String decisionBody = "{\"comment\":\"approved by security test\"}";

        assertForbidden(
                post("/purchase-requests/" + purchaseRequestId + "/approve", decisionBody, "EMPLOYEE"),
                "MANAGER role is required to approve or reject purchase requests"
        );

        HttpResponse<String> approved = post(
                "/purchase-requests/" + purchaseRequestId + "/approve",
                decisionBody,
                "MANAGER"
        );
        assertThat(approved.statusCode()).isEqualTo(200);
        assertThat(approved.body()).contains("APPROVED", "approved by security test");
    }

    private long createEmployee(String name, String department) throws IOException, InterruptedException {
        return json(post("/employees", employeeBody(name, department), "ADMIN")).get("id").asLong();
    }

    private JsonNode json(HttpResponse<String> response) throws IOException {
        assertThat(response.statusCode()).isBetween(200, 299);
        return objectMapper.readTree(response.body());
    }

    private void assertForbidden(
            HttpResponse<String> response,
            String expectedMessage
    ) {
        assertThat(response.statusCode()).isEqualTo(403);
        assertThat(response.body()).contains(expectedMessage);
    }

    private HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .GET()
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(
            String path,
            String body,
            String role
    ) throws IOException, InterruptedException {
        return sendWithBody("POST", path, body, role);
    }

    private HttpResponse<String> put(
            String path,
            String body,
            String role
    ) throws IOException, InterruptedException {
        return sendWithBody("PUT", path, body, role);
    }

    private HttpResponse<String> sendWithBody(
            String method,
            String path,
            String body,
            String role
    ) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .method(method, HttpRequest.BodyPublishers.ofString(body));
        if (role != null) {
            builder.header(RoleHeaderAuthenticationFilter.ROLE_HEADER, role);
        }
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private String employeeBody(String name, String department) {
        return """
                {"name":"%s","department":"%s"}
                """.formatted(name, department);
    }

    private String purchaseRequestBody(
            long employeeId,
            String description,
            BigDecimal amount,
            String status
    ) {
        return """
                {"employeeId":%d,"description":"%s","amount":%s,"status":"%s"}
                """.formatted(employeeId, description, amount, status);
    }
}
