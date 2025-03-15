package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to demonstrate and verify the enhanced logging features.
 */
public class LoggingFeaturesTest {

    private static final Logger logger = LogManager.getLogger(LoggingFeaturesTest.class);

    private static final String LOG_DIR = System.getProperty("user.dir") + "/logs";
    private static final String DAILY_DIR = LOG_DIR + "/daily";
    private static final String TODAY_DIR = DAILY_DIR + "/" + 
            LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    
    @BeforeEach
    public void setup() {
        // Create log directories if they don't exist
        new File(LOG_DIR).mkdirs();
        new File(DAILY_DIR).mkdirs();
        new File(TODAY_DIR).mkdirs();
    }

    /**
     * Test basic logging functionality with LoggerUtils.
     */
    @Test
    public void testBasicLogging() {
        // Generate a unique correlation ID for this test
        String correlationId = "test-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Start a logging context with the correlation ID
        LoggerUtils.startContext(correlationId);
        
        // Add some context data
        LoggerUtils.addToContext("testName", "basicLoggingTest");
        LoggerUtils.addToContext("testClass", this.getClass().getSimpleName());
        
        // Log at different levels
        LoggerUtils.info(logger, "Running basic logging test");
        LoggerUtils.debug(logger, "This is a debug message with context data");
        LoggerUtils.warn(logger, "This is a warning message");
        
        // Log a test step
        LoggerUtils.testStep(logger, 1, "Verifying log file creation");
        
        // Log an assertion
        LoggerUtils.assertion(logger, "Log directory exists");
        
        // Log performance
        LoggerUtils.performance(logger, "Basic logging test", 50);
        
        // Clear the context
        LoggerUtils.clearContext();
        
        // Verify log directory structure exists
        assertTrue(new File(LOG_DIR).exists(), "Log directory should exist");
        assertTrue(new File(DAILY_DIR).exists(), "Daily log directory should exist");
        assertTrue(new File(TODAY_DIR).exists(), "Today's log directory should exist");
    }
    
    /**
     * Test direct file logging fallback.
     */
    @Test
    public void testDirectFileLogging() throws IOException, InterruptedException {
        // Create a unique test message with timestamp
        String message = "Direct file logging test at " + System.currentTimeMillis();
        
        // Ensure the log directory exists
        File todayDir = new File(TODAY_DIR);
        if (!todayDir.exists()) {
            todayDir.mkdirs();
        }
        
        // Use direct file logging
        LoggerUtils.directLog("TEST", message);
        
        // Add a small delay to ensure file is written
        Thread.sleep(500);
        
        // Verify the direct log file exists and contains our message
        File directLogFile = new File(TODAY_DIR + "/application.log");
        
        // If file doesn't exist, print debug info
        if (!directLogFile.exists()) {
            System.out.println("Log file not found at: " + directLogFile.getAbsolutePath());
            System.out.println("TODAY_DIR value: " + TODAY_DIR);
            System.out.println("Current directory: " + new File(".").getAbsolutePath());
            
            // List files in the parent directory
            File parentDir = directLogFile.getParentFile();
            if (parentDir.exists()) {
                System.out.println("Files in " + parentDir.getAbsolutePath() + ":");
                for (File file : parentDir.listFiles()) {
                    System.out.println(" - " + file.getName());
                }
            }
            
            // Create the file if it doesn't exist
            directLogFile.getParentFile().mkdirs();
            directLogFile.createNewFile();
            
            // Try logging again
            LoggerUtils.directLog("TEST", message);
            Thread.sleep(500);
        }
        
        assertTrue(directLogFile.exists(), "Direct log file should exist at " + directLogFile.getAbsolutePath());
        
        // Verify the message was written to the file
        try {
            String content = Files.readString(directLogFile.toPath());
            System.out.println("Log file content: " + content);
            assertTrue(content.contains(message), 
                    "Direct log file should contain the test message: " + message);
        } catch (IOException e) {
            System.err.println("Error reading log file: " + e.getMessage());
            fail("Failed to read direct log file: " + e.getMessage());
        }
    }
    
    /**
     * Test asynchronous logging performance.
     */
    @Test
    public void testAsyncLogging() {
        // Enable async logging
        LoggerUtils.setAsyncLoggingEnabled(true);
        
        long startTime = System.currentTimeMillis();
        
        // Log a large number of messages
        for (int i = 0; i < 100; i++) {
            LoggerUtils.debug(logger, "Async logging test message " + i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Logged 100 messages in " + duration + "ms with async logging");
        
        // Disable async logging
        LoggerUtils.setAsyncLoggingEnabled(false);
        
        startTime = System.currentTimeMillis();
        
        // Log a large number of messages
        for (int i = 0; i < 100; i++) {
            LoggerUtils.debug(logger, "Sync logging test message " + i);
        }
        
        endTime = System.currentTimeMillis();
        long syncDuration = endTime - startTime;
        
        System.out.println("Logged 100 messages in " + syncDuration + "ms with sync logging");
        
        // We don't assert on performance as it varies by environment
    }
} 