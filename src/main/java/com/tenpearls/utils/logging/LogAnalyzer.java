package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Analyzes log files to identify patterns, errors, and potential issues.
 * Features:
 * - Error frequency analysis
 * - Performance bottleneck detection
 * - Correlation of related log entries
 * - Trend analysis for error rates
 * - Summary report generation
 */
@Component
public class LogAnalyzer {
    private static final Logger logger = LogManager.getLogger(LogAnalyzer.class);
    
    private static final String LOGS_DIR = "logs";
    private static final String DAILY_DIR = LOGS_DIR + File.separator + "daily";
    private static final String ANALYSIS_DIR = LOGS_DIR + File.separator + "analysis";
    
    // Patterns for log analysis
    private static final Pattern ERROR_PATTERN = Pattern.compile("(?i)\\b(error|exception|fail|failed|failure)\\b");
    private static final Pattern WARNING_PATTERN = Pattern.compile("(?i)\\b(warn|warning)\\b");
    private static final Pattern PERFORMANCE_PATTERN = Pattern.compile("(?i)\\bcompleted in (\\d+) ms\\b");
    private static final Pattern CORRELATION_ID_PATTERN = Pattern.compile("\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]");
    
    // In-memory cache for analysis results
    private final Map<String, Integer> errorFrequency = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> methodExecutionTimes = new ConcurrentHashMap<>();
    private final Map<String, Integer> warningFrequency = new ConcurrentHashMap<>();
    private final Set<String> uniqueExceptions = ConcurrentHashMap.newKeySet();
    
    /**
     * Scheduled task to analyze logs daily
     */
    @Scheduled(cron = "0 0 1 * * ?") // Run at 1:00 AM every day
    public void performDailyAnalysis() {
        logger.info("Starting daily log analysis");
        LoggerUtils.info(logger, "Starting daily log analysis");
        
        try {
            // Create analysis directory if it doesn't exist
            createDirectoryIfNotExists(ANALYSIS_DIR);
            
            // Get yesterday's date
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String yesterdayStr = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
            
            // Analyze yesterday's logs
            String yesterdayDir = DAILY_DIR + File.separator + yesterdayStr;
            File yesterdayDirFile = new File(yesterdayDir);
            
            if (yesterdayDirFile.exists() && yesterdayDirFile.isDirectory()) {
                // Clear previous analysis results
                clearAnalysisCache();
                
                // Analyze log files
                File[] logFiles = yesterdayDirFile.listFiles((dir, name) -> name.endsWith(".log") || name.endsWith(".txt"));
                if (logFiles != null) {
                    for (File logFile : logFiles) {
                        analyzeLogFile(logFile);
                    }
                }
                
                // Generate analysis report
                generateAnalysisReport(yesterdayStr);
                
                logger.info("Daily log analysis completed successfully");
                LoggerUtils.success(logger, "Daily log analysis completed successfully");
            } else {
                logger.warn("No logs found for yesterday ({})", yesterdayStr);
                LoggerUtils.warn(logger, "No logs found for yesterday (" + yesterdayStr + ")");
            }
        } catch (Exception e) {
            logger.error("Error during daily log analysis", e);
            LoggerUtils.error(logger, "Error during daily log analysis: " + e.getMessage());
        }
    }
    
    /**
     * Analyzes a log file for patterns and issues
     * @param logFile The log file to analyze
     */
    private void analyzeLogFile(File logFile) throws IOException {
        logger.debug("Analyzing log file: {}", logFile.getAbsolutePath());
        
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check for errors
                Matcher errorMatcher = ERROR_PATTERN.matcher(line);
                if (errorMatcher.find()) {
                    String errorType = extractErrorType(line);
                    errorFrequency.put(errorType, errorFrequency.getOrDefault(errorType, 0) + 1);
                    
                    // Extract exception name if present
                    if (line.contains("Exception:")) {
                        String exceptionName = extractExceptionName(line);
                        if (exceptionName != null) {
                            uniqueExceptions.add(exceptionName);
                        }
                    }
                }
                
                // Check for warnings
                Matcher warningMatcher = WARNING_PATTERN.matcher(line);
                if (warningMatcher.find()) {
                    String warningType = extractWarningType(line);
                    warningFrequency.put(warningType, warningFrequency.getOrDefault(warningType, 0) + 1);
                }
                
                // Check for performance metrics
                Matcher performanceMatcher = PERFORMANCE_PATTERN.matcher(line);
                if (performanceMatcher.find()) {
                    String methodName = extractMethodName(line);
                    long executionTime = Long.parseLong(performanceMatcher.group(1));
                    
                    methodExecutionTimes.computeIfAbsent(methodName, k -> new ArrayList<>()).add(executionTime);
                }
            }
        }
    }
    
    /**
     * Generates an analysis report based on the collected data
     * @param dateStr The date string for the report
     */
    private void generateAnalysisReport(String dateStr) throws IOException {
        String reportFileName = ANALYSIS_DIR + File.separator + "analysis_" + dateStr + ".txt";
        logger.info("Generating analysis report: {}", reportFileName);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFileName))) {
            writer.println("=== Log Analysis Report for " + dateStr + " ===");
            writer.println("Generated at: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.println();
            
            // Error analysis
            writer.println("=== ERROR ANALYSIS ===");
            writer.println("Total unique error types: " + errorFrequency.size());
            writer.println("Total error occurrences: " + errorFrequency.values().stream().mapToInt(Integer::intValue).sum());
            writer.println();
            writer.println("Top 10 most frequent errors:");
            errorFrequency.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> writer.println("  - " + entry.getKey() + ": " + entry.getValue() + " occurrences"));
            writer.println();
            
            // Warning analysis
            writer.println("=== WARNING ANALYSIS ===");
            writer.println("Total unique warning types: " + warningFrequency.size());
            writer.println("Total warning occurrences: " + warningFrequency.values().stream().mapToInt(Integer::intValue).sum());
            writer.println();
            writer.println("Top 10 most frequent warnings:");
            warningFrequency.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> writer.println("  - " + entry.getKey() + ": " + entry.getValue() + " occurrences"));
            writer.println();
            
            // Performance analysis
            writer.println("=== PERFORMANCE ANALYSIS ===");
            writer.println("Total methods with performance metrics: " + methodExecutionTimes.size());
            writer.println();
            writer.println("Top 10 slowest methods (average execution time):");
            methodExecutionTimes.entrySet().stream()
                    .sorted(Comparator.<Map.Entry<String, List<Long>>>comparingDouble(entry -> 
                            entry.getValue().stream().mapToLong(Long::longValue).average().orElse(0))
                            .reversed())
                    .limit(10)
                    .forEach(entry -> {
                        DoubleSummaryStatistics stats = entry.getValue().stream()
                                .mapToDouble(Long::doubleValue)
                                .summaryStatistics();
                        writer.println("  - " + entry.getKey() + ":");
                        writer.println("      Avg: " + String.format("%.2f", stats.getAverage()) + " ms");
                        writer.println("      Min: " + stats.getMin() + " ms");
                        writer.println("      Max: " + stats.getMax() + " ms");
                        writer.println("      Count: " + stats.getCount());
                    });
            writer.println();
            
            // Exception analysis
            writer.println("=== EXCEPTION ANALYSIS ===");
            writer.println("Total unique exceptions: " + uniqueExceptions.size());
            writer.println();
            writer.println("Unique exceptions encountered:");
            uniqueExceptions.stream()
                    .sorted()
                    .forEach(exception -> writer.println("  - " + exception));
            writer.println();
            
            // Recommendations
            writer.println("=== RECOMMENDATIONS ===");
            generateRecommendations(writer);
        }
        
        logger.info("Analysis report generated successfully");
    }
    
    /**
     * Generates recommendations based on the analysis results
     * @param writer The writer to write recommendations to
     */
    private void generateRecommendations(PrintWriter writer) {
        // Check for high-frequency errors
        List<Map.Entry<String, Integer>> highFrequencyErrors = errorFrequency.entrySet().stream()
                .filter(entry -> entry.getValue() >= 10) // More than 10 occurrences
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
        
        if (!highFrequencyErrors.isEmpty()) {
            writer.println("1. High-frequency errors that should be investigated:");
            highFrequencyErrors.forEach(entry -> 
                    writer.println("   - " + entry.getKey() + " (" + entry.getValue() + " occurrences)"));
            writer.println();
        }
        
        // Check for slow methods
        List<Map.Entry<String, List<Long>>> slowMethods = methodExecutionTimes.entrySet().stream()
                .filter(entry -> entry.getValue().stream().mapToLong(Long::longValue).average().orElse(0) > 1000) // More than 1 second avg
                .sorted(Comparator.<Map.Entry<String, List<Long>>>comparingDouble(entry -> 
                        entry.getValue().stream().mapToLong(Long::longValue).average().orElse(0))
                        .reversed())
                .collect(Collectors.toList());
        
        if (!slowMethods.isEmpty()) {
            writer.println("2. Slow methods that should be optimized:");
            slowMethods.forEach(entry -> {
                double avg = entry.getValue().stream().mapToLong(Long::longValue).average().orElse(0);
                writer.println("   - " + entry.getKey() + " (avg: " + String.format("%.2f", avg) + " ms)");
            });
            writer.println();
        }
        
        // Check for common exceptions
        if (!uniqueExceptions.isEmpty()) {
            writer.println("3. Exceptions that should be handled more gracefully:");
            uniqueExceptions.stream()
                    .limit(5)
                    .forEach(exception -> writer.println("   - " + exception));
            writer.println();
        }
    }
    
    /**
     * Extracts the error type from a log line
     * @param line The log line
     * @return The extracted error type
     */
    private String extractErrorType(String line) {
        // Try to extract a meaningful error type
        if (line.contains("Exception:")) {
            return extractExceptionName(line);
        } else if (line.contains("ERROR:")) {
            int index = line.indexOf("ERROR:");
            if (index >= 0 && index + 7 < line.length()) {
                String errorMessage = line.substring(index + 7).trim();
                // Take first 50 chars or up to first punctuation
                int endIndex = Math.min(50, errorMessage.length());
                for (int i = 0; i < endIndex; i++) {
                    if (errorMessage.charAt(i) == '.' || errorMessage.charAt(i) == '!' || errorMessage.charAt(i) == '?') {
                        endIndex = i;
                        break;
                    }
                }
                return errorMessage.substring(0, endIndex);
            }
        }
        
        // Default: return a substring of the line
        return line.length() > 100 ? line.substring(0, 100) + "..." : line;
    }
    
    /**
     * Extracts the exception name from a log line
     * @param line The log line
     * @return The extracted exception name
     */
    private String extractExceptionName(String line) {
        int exceptionIndex = line.indexOf("Exception:");
        if (exceptionIndex >= 0) {
            // Look for the exception class name before "Exception:"
            int startIndex = line.lastIndexOf('.', exceptionIndex);
            if (startIndex >= 0) {
                return line.substring(startIndex + 1, exceptionIndex + 10);
            } else {
                // Try to find the start of the exception name
                startIndex = exceptionIndex;
                while (startIndex > 0 && Character.isLetterOrDigit(line.charAt(startIndex - 1))) {
                    startIndex--;
                }
                return line.substring(startIndex, exceptionIndex + 10);
            }
        }
        return null;
    }
    
    /**
     * Extracts the warning type from a log line
     * @param line The log line
     * @return The extracted warning type
     */
    private String extractWarningType(String line) {
        if (line.contains("WARNING:")) {
            int index = line.indexOf("WARNING:");
            if (index >= 0 && index + 9 < line.length()) {
                String warningMessage = line.substring(index + 9).trim();
                // Take first 50 chars or up to first punctuation
                int endIndex = Math.min(50, warningMessage.length());
                for (int i = 0; i < endIndex; i++) {
                    if (warningMessage.charAt(i) == '.' || warningMessage.charAt(i) == '!' || warningMessage.charAt(i) == '?') {
                        endIndex = i;
                        break;
                    }
                }
                return warningMessage.substring(0, endIndex);
            }
        }
        
        // Default: return a substring of the line
        return line.length() > 100 ? line.substring(0, 100) + "..." : line;
    }
    
    /**
     * Extracts the method name from a log line
     * @param line The log line
     * @return The extracted method name
     */
    private String extractMethodName(String line) {
        if (line.contains("Method [")) {
            int startIndex = line.indexOf("Method [");
            int endIndex = line.indexOf("]", startIndex);
            if (startIndex >= 0 && endIndex > startIndex) {
                return line.substring(startIndex + 8, endIndex);
            }
        }
        
        // Default: return a substring of the line
        return line.length() > 100 ? line.substring(0, 100) + "..." : line;
    }
    
    /**
     * Clears the analysis cache
     */
    private void clearAnalysisCache() {
        errorFrequency.clear();
        methodExecutionTimes.clear();
        warningFrequency.clear();
        uniqueExceptions.clear();
    }
    
    /**
     * Creates a directory if it doesn't exist
     * @param dirPath The directory path to create
     */
    private void createDirectoryIfNotExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.debug("Created directory: {}", dirPath);
        }
    }
    
    /**
     * Provides a summary of the most recent log analysis
     * @return A summary of the analysis results
     */
    public Map<String, Object> getAnalysisSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Get the most recent analysis file
        File analysisDir = new File(ANALYSIS_DIR);
        if (analysisDir.exists() && analysisDir.isDirectory()) {
            File[] analysisFiles = analysisDir.listFiles((dir, name) -> name.startsWith("analysis_") && name.endsWith(".txt"));
            if (analysisFiles != null && analysisFiles.length > 0) {
                // Sort by last modified time (descending)
                Arrays.sort(analysisFiles, Comparator.comparing(File::lastModified).reversed());
                
                // Get the most recent file
                File mostRecentFile = analysisFiles[0];
                summary.put("fileName", mostRecentFile.getName());
                summary.put("fileDate", new Date(mostRecentFile.lastModified()));
                
                // Extract key metrics from the file
                try {
                    Map<String, Object> metrics = extractMetricsFromAnalysisFile(mostRecentFile);
                    summary.putAll(metrics);
                } catch (IOException e) {
                    logger.error("Error extracting metrics from analysis file", e);
                }
            }
        }
        
        return summary;
    }
    
    /**
     * Extracts key metrics from an analysis file
     * @param analysisFile The analysis file
     * @return A map of key metrics
     */
    private Map<String, Object> extractMetricsFromAnalysisFile(File analysisFile) throws IOException {
        Map<String, Object> metrics = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(analysisFile))) {
            String line;
            String currentSection = null;
            
            while ((line = reader.readLine()) != null) {
                // Check for section headers
                if (line.startsWith("=== ") && line.endsWith(" ===")) {
                    currentSection = line.substring(4, line.length() - 4).trim();
                    continue;
                }
                
                // Extract metrics based on the current section
                if (currentSection != null) {
                    if (currentSection.equals("ERROR ANALYSIS")) {
                        if (line.startsWith("Total unique error types:")) {
                            metrics.put("uniqueErrorTypes", extractNumber(line));
                        } else if (line.startsWith("Total error occurrences:")) {
                            metrics.put("totalErrors", extractNumber(line));
                        }
                    } else if (currentSection.equals("WARNING ANALYSIS")) {
                        if (line.startsWith("Total unique warning types:")) {
                            metrics.put("uniqueWarningTypes", extractNumber(line));
                        } else if (line.startsWith("Total warning occurrences:")) {
                            metrics.put("totalWarnings", extractNumber(line));
                        }
                    } else if (currentSection.equals("PERFORMANCE ANALYSIS")) {
                        if (line.startsWith("Total methods with performance metrics:")) {
                            metrics.put("methodsWithPerformanceMetrics", extractNumber(line));
                        }
                    } else if (currentSection.equals("EXCEPTION ANALYSIS")) {
                        if (line.startsWith("Total unique exceptions:")) {
                            metrics.put("uniqueExceptions", extractNumber(line));
                        }
                    }
                }
            }
        }
        
        return metrics;
    }
    
    /**
     * Extracts a number from a line of text
     * @param line The line of text
     * @return The extracted number, or -1 if no number is found
     */
    private int extractNumber(String line) {
        int colonIndex = line.indexOf(':');
        if (colonIndex >= 0 && colonIndex + 1 < line.length()) {
            String numberStr = line.substring(colonIndex + 1).trim();
            try {
                return Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        return -1;
    }
} 