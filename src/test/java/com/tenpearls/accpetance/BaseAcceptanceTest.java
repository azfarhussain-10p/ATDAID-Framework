package com.tenpearls.accpetance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseAcceptanceTest {

    @LocalServerPort
    private int port = 8080; // Default to 8080 if not injected
    
    protected RequestSpecification given() {
        return RestAssured.given()
                .baseUri("http://localhost")
                .port(port)
                .contentType("application/json");
    }
    
    @BeforeClass
    public void setUp() {
        // Setup test environment
        RestAssured.filters(new io.restassured.filter.log.RequestLoggingFilter(), new io.restassured.filter.log.ResponseLoggingFilter());
        
        // Print the port being used
        System.out.println("Using port: " + port);
    }
    
    @BeforeMethod
    public void resetTestData() {
        // Reset test data before each test
        // This could connect to your test database and reset state
    }
    
    @AfterClass
    public void tearDown() {
        // Clean up resources
    }
}