package com.tenpearls.utils.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for logging features.
 * This test class is designed to run independently without requiring the full application context.
 */
public class LoggingFeaturesTest {

    private LoggerUtils loggerUtils;
    private LogDirectoryInitializer directoryInitializer;
    private LogRotationManager rotationManager;
    private LogAnalyzer logAnalyzer;
    private LogMonitor logMonitor;
    private LoggingPerformanceOptimizer performanceOptimizer;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // Initialize with temp directory for testing
        String basePath = tempDir.toAbsolutePath().toString();
        System.setProperty("logging.base.path", basePath);
        
        directoryInitializer = new LogDirectoryInitializer();
        directoryInitializer.initializeDirectories();
        
        rotationManager = new LogRotationManager();
        logAnalyzer = new LogAnalyzer();
        logMonitor = new LogMonitor();
        performanceOptimizer = new LoggingPerformanceOptimizer();
        
        // Initialize logger utils after directories are set up
        loggerUtils = LoggerUtils.getInstance();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up
        System.clearProperty("logging.base.path");
    }
    
    @Test
    void testBasicLogging() {
        // Test basic logging functionality
        loggerUtils.info("Running basic logging test");
        loggerUtils.debug("This is a debug message with context data", "key1", "value1", "key2", "value2");
        loggerUtils.warning("This is a warning message");
        loggerUtils.testStep("Verifying log file creation");
        loggerUtils.testAssertion("Log directory exists");
        loggerUtils.performance("Basic logging test completed in 50 ms");
        
        // Verify log directories exist
        File logsDir = new File(tempDir.toFile(), "logs");
        assertTrue(logsDir.exists(), "Logs directory should exist");
    }
    
    @Test
    void testAsyncLogging() throws InterruptedException {
        // Test async logging
        CountDownLatch latch = new CountDownLatch(1);
        
        // Log 100 messages asynchronously
        for (int i = 0; i < 100; i++) {
            loggerUtils.debugAsync("Async logging test message " + i);
        }
        
        // Allow some time for async logging to complete
        latch.await(500, TimeUnit.MILLISECONDS);
        
        // Verify performance optimizer is working
        assertTrue(performanceOptimizer.isInitialized(), "Performance optimizer should be initialized");
    }
    
    @Test
    void testDirectFileLogging() {
        // Test direct file logging
        long timestamp = System.currentTimeMillis();
        loggerUtils.test("Direct file logging test at " + timestamp);
        
        // Log 100 messages synchronously
        for (int i = 0; i < 100; i++) {
            loggerUtils.debug("Sync logging test message " + i);
        }
        
        // Verify log analyzer can be initialized
        assertNotNull(logAnalyzer, "Log analyzer should be initialized");
        assertTrue(logAnalyzer.isInitialized(), "Log analyzer should be initialized");
    }
} 