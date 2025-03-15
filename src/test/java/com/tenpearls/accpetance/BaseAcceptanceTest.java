package com.tenpearls.accpetance;

import com.tenpearls.base.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Base class for all acceptance tests.
 * Extends BaseTest to integrate with Log4j2 and ExtentReports.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseAcceptanceTest extends BaseTest {

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
        logger.info("Setting up BaseAcceptanceTest");
        
        // Setup test environment
        RestAssured.filters(new io.restassured.filter.log.RequestLoggingFilter(), new io.restassured.filter.log.ResponseLoggingFilter());
        
        // Print the port being used
        logger.info("Using port: {}", port);
        
        logger.debug("BaseAcceptanceTest setup completed");
    }
    
    @BeforeMethod
    public void resetTestData() {
        logger.info("Resetting test data");
        // Reset test data before each test
        // This could connect to your test database and reset state
        
        logger.debug("Test data reset completed");
    }
    
    @AfterClass
    public void tearDown() {
        logger.info("Tearing down BaseAcceptanceTest");
        // Clean up resources
        
        logger.debug("BaseAcceptanceTest teardown completed");
    }
}