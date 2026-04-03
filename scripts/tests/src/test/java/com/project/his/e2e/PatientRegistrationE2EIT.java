package com.project.his.e2e;

import com.project.his.e2e.dto.AuthRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientRegistrationE2EIT extends BaseE2ETest {

    private static String adminToken;
    private static String createdPatientId;
    private static final String ADMIN_USER = "Test_" + System.currentTimeMillis() + "_Admin";
    private static final String ADMIN_PASS = "AdminPass123!";

    @BeforeAll
    public void init() {
        super.setup();
        // 1. Register the admin account dynamically
        AuthRequest registerRequest = AuthRequest.builder()
                .name(ADMIN_USER)
                .email("admin_test_" + System.currentTimeMillis() + "@his.com")
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

        // 2. Login to get the elevated token
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
        
        System.out.println("E2E Test: Elevated Admin Token acquired.");
    }

    @Test
    @Order(1)
    @DisplayName("Elevated Admin should successfully register a new patient profile")
    public void shouldRegisterPatient() {
        String patientJson = "{" +
                "\"name\": \"E2E Integration Patient\"," +
                "\"email\": \"e2e_pt_" + System.currentTimeMillis() + "@gmail.com\"," +
                "\"address\": \"456 Testing Blvd\"," +
                "\"dateOfBirth\": \"1985-05-15\"," +
                "\"registeredDate\": \"2026-04-03\"" +
                "}";

        createdPatientId = given()
                .header("Authorization", getAuthHeader(adminToken))
                .contentType(ContentType.JSON)
                .body(patientJson)
            .when()
                .post("/api/patients")
            .then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract().path("id");
    }

    @Test
    @Order(2)
    @DisplayName("Admin should find the registered patient by ID")
    public void shouldFindPatient() {
        given()
                .header("Authorization", getAuthHeader(adminToken))
            .when()
                .get("/api/patients/find/" + createdPatientId)
            .then()
                .statusCode(200)
                .body("name", is("E2E Integration Patient"))
                .body("email", containsString("@gmail.com"));
    }
}
