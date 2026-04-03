package com.project.his.e2e;

import com.project.his.e2e.dto.AuthRequest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthE2EIT extends BaseE2ETest {

    private static String accessToken;
    private static final String TEST_USER = "E2ETestUser_" + System.currentTimeMillis();
    private static final String TEST_EMAIL = "e2e_" + System.currentTimeMillis() + "@hospital.com";
    private static final String TEST_PASS = "Pass123!_Hardened";

    @Test
    @Order(1)
    @DisplayName("Should successfully register a new administrative user")
    public void shouldRegisterNewUser() {
        AuthRequest registerRequest = AuthRequest.builder()
                .name(TEST_USER)
                .email(TEST_EMAIL)
                .password(TEST_PASS)
                .registerDate("2026-04-03")
                .build();

        accessToken = given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
            .when()
                .post("/api/auth/register")
            .then()
                .statusCode(200)
                .body("message", containsString("successfully"))
                .body("token", notNullValue())
                .extract().path("token");
    }

    @Test
    @Order(2)
    @DisplayName("Should successfully login with the new credentials")
    public void shouldLoginUser() {
        AuthRequest loginRequest = AuthRequest.builder()
                .name(TEST_USER)
                .password(TEST_PASS)
                .build();

        String token = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
            .when()
                .post("/api/auth/login")
            .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().path("token");
        
        // Assert that a fresh token is generated
        assert(token != null && !token.isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("Should fail login with incorrect credentials")
    public void shouldFailOnWrongPassword() {
        AuthRequest badRequest = AuthRequest.builder()
                .name(TEST_USER)
                .password("WrongPassword")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(badRequest)
            .when()
                .post("/api/auth/login")
            .then()
                .statusCode(401); // Assuming 401 for unauthorized
    }
}
