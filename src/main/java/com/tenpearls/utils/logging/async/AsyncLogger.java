package com.tenpearls.utils.logging.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous logging functionality that provides non-blocking logging operations.
 * This class uses a thread pool to process log entries asynchronously.
 */
@Component
public class AsyncLogger {
    
    private static final Logger logger = LogManager.getLogger(AsyncLogger.class);
    
    @Value("${logging.async.queue.size:1000}")
    private int queueSize;
    
    @Value("${logging.async.thread.pool.size:2}")
    private int threadPoolSize;
    
    private ExecutorService executorService;
    private BlockingQueue<LogEntry> logQueue;
    private AtomicInteger droppedLogCount;
    private volatile boolean running;
    
    /**
     * Initialize the async logger
     */
    @PostConstruct
    public void init() {
        logQueue = new LinkedBlockingQueue<>(queueSize);
        executorService = Executors.newFixedThreadPool(threadPoolSize);
        droppedLogCount = new AtomicInteger(0);
        running = true;
        
        // Start worker threads
        for (int i = 0; i < threadPoolSize; i++) {
            executorService.submit(this::processLogEntries);
        }
        
        logger.info("AsyncLogger initialized with queue size {} and thread pool size {}", 
                queueSize, threadPoolSize);
    }
    
    /**
     * Cleanup resources on shutdown
     */
    @PreDestroy
    public void cleanup() {
        running = false;
        executorService.shutdown();
        logger.info("AsyncLogger shutting down, dropped log count: {}", droppedLogCount.get());
    }
    
    /**
     * Log a message asynchronously at the specified level
     * 
     * @param level The log level
     * @param loggerName The logger name
     * @param message The message to log
     */
    public void asyncLog(Level level, String loggerName, String message) {
        LogEntry entry = new LogEntry(level, loggerName, message, (Object[]) null);
        offerLogEntry(entry, level);
    }
    
    /**
     * Log a message with parameters asynchronously at the specified level
     * 
     * @param level The log level
     * @param loggerName The logger name
     * @param message The message format
     * @param params The parameters for the message
     */
    public void asyncLog(Level level, String loggerName, String message, Object... params) {
        LogEntry entry = new LogEntry(level, loggerName, message, params);
        offerLogEntry(entry, level);
    }
    
    /**
     * Log a message with an exception asynchronously at the specified level
     * 
     * @param level The log level
     * @param loggerName The logger name
     * @param message The message
     * @param throwable The exception to log
     */
    public void asyncLog(Level level, String loggerName, String message, Throwable throwable) {
        LogEntry entry = new LogEntry(level, loggerName, message, throwable);
        offerLogEntry(entry, level);
    }
    
    /**
     * Offer a log entry to the queue
     * 
     * @param entry The log entry
     * @param level The log level
     */
    private void offerLogEntry(LogEntry entry, Level level) {
        // Try to add to queue, but don't block if full
        boolean added = logQueue.offer(entry);
        if (!added) {
            // If queue is full, increment dropped count
            droppedLogCount.incrementAndGet();
            
            // Log directly if it's an important message
            if (level.equals(Level.ERROR) || level.equals(Level.FATAL)) {
                Logger targetLogger = LogManager.getLogger(entry.getLoggerName());
                targetLogger.log(level, "[DIRECT] (Queue full): {}", entry.getMessage());
            }
        }
    }
    
    /**
     * Process log entries from the queue
     */
    private void processLogEntries() {
        while (running) {
            try {
                LogEntry entry = logQueue.take();
                processLogEntry(entry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("AsyncLogger worker thread interrupted", e);
                break;
            } catch (Exception e) {
                logger.error("Error processing log entry", e);
            }
        }
    }
    
    /**
     * Process a single log entry
     * 
     * @param entry The log entry to process
     */
    private void processLogEntry(LogEntry entry) {
        Logger targetLogger = LogManager.getLogger(entry.getLoggerName());
        
        if (entry.getParams() != null) {
            targetLogger.log(entry.getLevel(), entry.getMessage(), entry.getParams());
        } else if (entry.getThrowable() != null) {
            targetLogger.log(entry.getLevel(), entry.getMessage(), entry.getThrowable());
        } else {
            targetLogger.log(entry.getLevel(), entry.getMessage());
        }
    }
    
    /**
     * Get the number of dropped log entries
     * 
     * @return The number of dropped log entries
     */
    public int getDroppedLogCount() {
        return droppedLogCount.get();
    }
    
    /**
     * Get the current queue size
     * 
     * @return The current queue size
     */
    public int getCurrentQueueSize() {
        return logQueue.size();
    }
} 