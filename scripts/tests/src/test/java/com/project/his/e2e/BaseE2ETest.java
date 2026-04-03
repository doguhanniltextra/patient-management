package com.project.his.e2e;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseE2ETest {

    private static final Logger log = LoggerFactory.getLogger(BaseE2ETest.class);
    
    // De-facto approach for local microservices: Target the running Gateway.
    // In CI/CD, this is overridden by system properties or environment variables.
    protected static String BASE_URL = System.getProperty("e2e.base.url", "http://localhost:4004");

    @BeforeAll
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        
        // Enabling detailed logging for "tons of tests" audit
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        
        log.info("End-to-End Test Suite starting. Target: {}", RestAssured.baseURI);
    }

    protected String getAuthHeader(String token) {
        return "Bearer " + token;
    }
}
