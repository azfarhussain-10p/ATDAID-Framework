package com.tenpearls.utils.logging.performance;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Processes log entries in batches for better performance.
 * This class collects log entries and processes them in batches to reduce I/O operations.
 * It includes optimizations such as logger caching, efficient batch processing, and thread safety.
 */
@Component
public class BatchProcessor {
    
    private static final Logger logger = LogManager.getLogger(BatchProcessor.class);
    
    @Value("${logging.batch.size:50}")
    private int batchSize;
    
    @Value("${logging.batch.queue.size:1000}")
    private int queueSize;
    
    @Value("${logging.batch.flush.interval:5000}")
    private long flushIntervalMs;
    
    private final BlockingQueue<BatchEntry> entryQueue;
    private final AtomicInteger droppedEntryCount;
    private volatile boolean running;
    
    // Cache loggers to avoid repeated lookups
    private final Map<String, Logger> loggerCache = new ConcurrentHashMap<>();
    
    // Lock for batch processing to ensure thread safety
    private final ReentrantLock processingLock = new ReentrantLock();
    
    /**
     * Constructor that initializes the batch processor with default values
     * and creates the necessary data structures.
     */
    public BatchProcessor() {
        // Default values in case @Value injection hasn't happened yet
        this.queueSize = 1000; // Default queue size
        this.batchSize = 50;   // Default batch size
        this.flushIntervalMs = 5000; // Default flush interval
        
        entryQueue = new LinkedBlockingQueue<>(queueSize);
        droppedEntryCount = new AtomicInteger(0);
        running = true;
        logger.info("Batch processor initialized");
    }
    
    /**
     * Initialize the batch processor after dependency injection
     */
    @PostConstruct
    public void init() {
        logger.info("Batch processor started with batch size {}, queue size {}, and flush interval {} ms",
                batchSize, queueSize, flushIntervalMs);
    }
    
    /**
     * Cleanup resources on shutdown and process any remaining entries
     */
    @PreDestroy
    public void cleanup() {
        running = false;
        logger.info("Batch processor shutting down, processing remaining entries...");
        processBatch();
        logger.info("Batch processor shutdown complete, dropped entry count: {}", droppedEntryCount.get());
        
        // Clear the logger cache
        loggerCache.clear();
    }
    
    /**
     * Add an entry to the batch queue
     * 
     * @param level The log level
     * @param loggerName The logger name
     * @param message The message
     * @param params The parameters for the message (can be null)
     * @return true if the entry was added, false if the queue is full
     */
    public boolean addEntry(Level level, String loggerName, String message, Object[] params) {
        if (!running) {
            return false; // Don't accept new entries if shutting down
        }
        
        BatchEntry entry = new BatchEntry(level, loggerName, message, params);
        boolean added = entryQueue.offer(entry);
        
        if (!added) {
            droppedEntryCount.incrementAndGet();
            
            // Log directly if it's an important message
            if (level.equals(Level.ERROR) || level.equals(Level.FATAL)) {
                Logger targetLogger = getOrCreateLogger(loggerName);
                targetLogger.log(level, "[DIRECT] (Queue full): {}", message);
            }
        }
        
        return added;
    }
    
    /**
     * Process a batch of entries
     * This method is scheduled to run at a fixed interval
     * It uses a lock to ensure thread safety during processing
     */
    @Scheduled(fixedRateString = "${logging.batch.flush.interval:5000}")
    public void processBatch() {
        if (!running) {
            return; // Don't process if shutting down
        }
        
        // Use a lock to ensure only one thread processes the batch at a time
        if (!processingLock.tryLock()) {
            return; // Another thread is already processing
        }
        
        try {
            List<BatchEntry> batch = new ArrayList<>(batchSize);
            entryQueue.drainTo(batch, batchSize);
            
            if (!batch.isEmpty()) {
                for (BatchEntry entry : batch) {
                    processEntry(entry);
                }
                
                logger.debug("Processed batch of {} entries", batch.size());
            }
        } finally {
            processingLock.unlock();
        }
    }
    
    /**
     * Process a single entry
     * Uses the logger cache for better performance
     * 
     * @param entry The entry to process
     */
    private void processEntry(BatchEntry entry) {
        Logger targetLogger = getOrCreateLogger(entry.getLoggerName());
        
        if (entry.getParams() != null) {
            targetLogger.log(entry.getLevel(), entry.getMessage(), entry.getParams());
        } else {
            targetLogger.log(entry.getLevel(), entry.getMessage());
        }
    }
    
    /**
     * Get a logger from the cache or create a new one if not found
     * 
     * @param loggerName The name of the logger
     * @return The logger instance
     */
    private Logger getOrCreateLogger(String loggerName) {
        return loggerCache.computeIfAbsent(loggerName, LogManager::getLogger);
    }
    
    /**
     * Get the number of dropped entries
     * 
     * @return The number of dropped entries
     */
    public int getDroppedEntryCount() {
        return droppedEntryCount.get();
    }
    
    /**
     * Get the current queue size
     * 
     * @return The current queue size
     */
    public int getCurrentQueueSize() {
        return entryQueue.size();
    }
    
    /**
     * Force processing of all entries in the queue
     * Useful for testing or when immediate processing is required
     * 
     * @return The number of entries processed
     */
    public int forceProcessAll() {
        if (processingLock.tryLock()) {
            try {
                List<BatchEntry> allEntries = new ArrayList<>(entryQueue.size());
                entryQueue.drainTo(allEntries);
                
                if (!allEntries.isEmpty()) {
                    for (BatchEntry entry : allEntries) {
                        processEntry(entry);
                    }
                    
                    logger.debug("Force processed {} entries", allEntries.size());
                    return allEntries.size();
                }
                
                return 0;
            } finally {
                processingLock.unlock();
            }
        }
        
        return -1; // Could not acquire lock
    }
    
    /**
     * Represents a batch entry
     */
    private static class BatchEntry {
        private final Level level;
        private final String loggerName;
        private final String message;
        private final Object[] params;
        
        /**
         * Constructor for a batch entry
         * 
         * @param level The log level
         * @param loggerName The logger name
         * @param message The message
         * @param params The parameters for the message (can be null)
         */
        public BatchEntry(Level level, String loggerName, String message, Object[] params) {
            this.level = level;
            this.loggerName = loggerName;
            this.message = message;
            this.params = params;
        }
        
        /**
         * Get the log level
         * 
         * @return The log level
         */
        public Level getLevel() {
            return level;
        }
        
        /**
         * Get the logger name
         * 
         * @return The logger name
         */
        public String getLoggerName() {
            return loggerName;
        }
        
        /**
         * Get the message
         * 
         * @return The message
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * Get the parameters
         * 
         * @return The parameters
         */
        public Object[] getParams() {
            return params;
        }
    }
} 