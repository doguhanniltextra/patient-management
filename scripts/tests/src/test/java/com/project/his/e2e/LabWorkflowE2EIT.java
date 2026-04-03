package com.project.his.e2e;

import com.project.his.e2e.dto.AuthRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LabWorkflowE2EIT extends BaseE2ETest {

    private static String adminToken;
    private static String patientId;
    private static String orderId;
    private static final String ADMIN_USER = "LabSuite_" + System.currentTimeMillis() + "_Admin";
    private static final String ADMIN_PASS = "Pass123!";

    @BeforeAll
    public void init() {
        super.setup();
        
        // 1. Get Admin Token
        AuthRequest registerRequest = AuthRequest.builder()
                .name(ADMIN_USER)
                .email("lab_" + System.currentTimeMillis() + "@his.com")
                .password(ADMIN_PASS)
                .registerDate("2026-04-03")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
            .when()
                .post("/api/auth/register")
            .then()
                .statusCode(200);

        AuthRequest loginRequest = AuthRequest.builder()
                .name(ADMIN_USER)
                .password(ADMIN_PASS)
                .build();

        adminToken = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
            .when()
                .post("/api/auth/login")
            .then()
                .statusCode(200)
                .extract().path("token");

        // 2. Register a Patient to order for
        String patientJson = "{" +
                "\"name\": \"Lab Test Patient\"," +
                "\"email\": \"lab_pt_" + System.currentTimeMillis() + "@gmail.com\"," +
                "\"address\": \"789 Lab St\"," +
                "\"dateOfBirth\": \"2000-01-01\"," +
                "\"registeredDate\": \"2026-04-03\"" +
                "}";

        patientId = given()
                .header("Authorization", getAuthHeader(adminToken))
                .contentType(ContentType.JSON)
                .body(patientJson)
            .when()
                .post("/api/patients")
            .then()
                .statusCode(200)
                .extract().path("id");
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully place a lab order via Doctor Service")
    public void shouldPlaceLabOrder() {
        UUID doctorId = UUID.fromString("d8768074-ce44-4630-9372-570a2569f697"); // Use a fixed UUID for test
        
        String orderJson = "{" +
                "\"patientId\": \"" + patientId + "\"," +
                "\"tests\": [" + 
                "  {\"testCode\": \"LB001\", \"priority\": \"HIGH\", \"unitPrice\": 55.0, \"quantity\": 1}" +
                "]," +
                "\"requestedAt\": \"2026-04-03T10:00:00\"" +
                "}";

        orderId = given()
                .header("Authorization", getAuthHeader(adminToken))
                .contentType(ContentType.JSON)
                .body(orderJson)
            .when()
                .post("/api/doctors/" + doctorId + "/lab-orders")
            .then()
                .statusCode(200)
                .body("orderId", notNullValue())
                .extract().path("orderId");
        
        System.out.println("E2E Test: Lab Order PLACED via Doctor Service. ID: " + orderId);
    }

    @Test
    @Order(2)
    @DisplayName("Should find the order in Lab Service (Kafka Propagation check)")
    public void shouldFindOrderInLabService() throws InterruptedException {
        // Wait for Outbox -> Kafka -> LabService sync (Doctor Service relay runs every 5s)
        Thread.sleep(10000);

        given()
                .header("Authorization", getAuthHeader(adminToken))
            .when()
                .get("/api/labs/orders/" + orderId)
            .then()
                .statusCode(200)
                .body("patientId", is(patientId))
                .body("status", is("QUEUED"));
    }

    @Test
    @Order(3)
    @DisplayName("Should complete the lab order and verify status transition")
    public void shouldCompleteLabOrder() {
        String completeJson = "{" +
                "\"results\": [" +
                "  {\"testCode\": \"LB001\", \"value\": \"12.5\", \"unit\": \"mg/dL\", \"referenceRange\": \"5.0-15.0\", \"abnormalFlag\": \"NORMAL\"}" +
                "]," +
                "\"reportPdfUrl\": \"http://storage.his.com/reports/123.pdf\"," +
                "\"correlationId\": \"E2E-123\"" +
                "}";

        given()
                .header("Authorization", getAuthHeader(adminToken))
                .contentType(ContentType.JSON)
                .body(completeJson)
            .when()
                .put("/api/labs/orders/" + orderId + "/complete")
            .then()
                .statusCode(200)
                .body("status", is("COMPLETED"));
    }

    @Test
    @Order(4)
    @DisplayName("Should find the results in Patient Management service")
    public void shouldFindResultsInPatientService() throws InterruptedException {
        // Wait for Result Event -> Patient Service sync
        Thread.sleep(10000);

        given()
                .header("Authorization", getAuthHeader(adminToken))
            .when()
                .get("/api/patients/" + patientId + "/lab-results")
            .then()
                .statusCode(200)
                .body("[0].value", is("12.5"))
                .body("[0].unit", is("mg/dL"));
    }
}
