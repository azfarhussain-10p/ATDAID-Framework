package com.tenpearls.utils.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Monitors log files for critical errors and sends email notifications.
 * Scheduled to run at regular intervals to check for new critical log entries.
 */
@Component
public class LogMonitor {
    private static final Logger logger = LogManager.getLogger(LogMonitor.class);
    private static final String LOG_DIR = System.getProperty("user.dir") + "/logs";
    private static final String DAILY_DIR = LOG_DIR + "/daily";
    private static final Pattern ERROR_PATTERN = Pattern.compile("\\[(ERROR|CRITICAL)\\]");
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile("Exception|Error|Throwable");
    
    private LocalDateTime lastCheckTime = LocalDateTime.now();
    
    @Value("${logging.monitor.enabled:false}")
    private boolean monitoringEnabled;
    
    @Value("${logging.monitor.email.enabled:false}")
    private boolean emailEnabled;
    
    @Value("${logging.monitor.email.to:admin@example.com}")
    private String emailTo;
    
    @Value("${logging.monitor.email.from:system@example.com}")
    private String emailFrom;
    
    @Value("${logging.monitor.email.subject:[ALERT] Log Monitoring Alert}")
    private String emailSubject;
    
    @Value("${logging.monitor.check.interval:3600000}")
    private long checkInterval;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    /**
     * Scheduled method to check logs for critical errors.
     * Runs at a fixed rate defined by logging.monitor.check.interval property.
     */
    @Scheduled(fixedRateString = "${logging.monitor.check.interval:3600000}")
    public void checkLogsForErrors() {
        if (!monitoringEnabled) {
            logger.debug("Log monitoring is disabled");
            return;
        }
        
        logger.info("Starting scheduled log check for errors at {}", 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        try {
            List<String> criticalErrors = findCriticalErrors();
            
            if (!criticalErrors.isEmpty()) {
                logger.warn("Found {} critical errors in logs", criticalErrors.size());
                sendAlertEmail(criticalErrors);
            } else {
                logger.info("No critical errors found in logs since last check");
            }
        } catch (Exception e) {
            logger.error("Error while checking logs for critical errors", e);
        }
        
        lastCheckTime = LocalDateTime.now();
    }
    
    /**
     * Finds critical errors in log files that occurred since the last check.
     * 
     * @return List of critical error messages
     */
    private List<String> findCriticalErrors() {
        List<String> errors = new ArrayList<>();
        
        // Check today's log directory
        String todayDir = DAILY_DIR + "/" + LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        
        try {
            // Check main log file
            File mainLogFile = new File(LOG_DIR + "/atdaid.log");
            if (mainLogFile.exists()) {
                errors.addAll(findErrorsInFile(mainLogFile));
            }
            
            // Check today's log files
            File todayDirFile = new File(todayDir);
            if (todayDirFile.exists() && todayDirFile.isDirectory()) {
                try (Stream<Path> paths = Files.walk(Paths.get(todayDir))) {
                    List<File> logFiles = paths
                            .filter(Files::isRegularFile)
                            .map(Path::toFile)
                            .collect(Collectors.toList());
                    
                    for (File logFile : logFiles) {
                        errors.addAll(findErrorsInFile(logFile));
                    }
                }
            }
            
            // Check direct log file
            File directLogFile = new File(LOG_DIR + "/direct_log.txt");
            if (directLogFile.exists()) {
                errors.addAll(findErrorsInFile(directLogFile));
            }
        } catch (IOException e) {
            logger.error("Error while searching for critical errors in log files", e);
        }
        
        return errors;
    }
    
    /**
     * Finds error messages in a specific log file.
     * 
     * @param logFile The log file to check
     * @return List of error messages found in the file
     */
    private List<String> findErrorsInFile(File logFile) {
        List<String> errors = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            StringBuilder currentError = null;
            boolean inErrorBlock = false;
            
            while ((line = reader.readLine()) != null) {
                // Check if this line contains a timestamp that's after our last check
                if (containsTimestampAfter(line, lastCheckTime)) {
                    Matcher errorMatcher = ERROR_PATTERN.matcher(line);
                    Matcher exceptionMatcher = EXCEPTION_PATTERN.matcher(line);
                    
                    if (errorMatcher.find() || exceptionMatcher.find()) {
                        if (!inErrorBlock) {
                            inErrorBlock = true;
                            currentError = new StringBuilder();
                        }
                        currentError.append(line).append("\n");
                    } else if (inErrorBlock) {
                        // Continue capturing stack trace lines
                        if (line.trim().startsWith("at ") || line.trim().startsWith("Caused by:")) {
                            currentError.append(line).append("\n");
                        } else {
                            // End of error block
                            inErrorBlock = false;
                            errors.add(currentError.toString());
                            currentError = null;
                        }
                    }
                }
            }
            
            // Add the last error if we were in an error block
            if (inErrorBlock && currentError != null) {
                errors.add(currentError.toString());
            }
        } catch (IOException e) {
            logger.error("Error reading log file: {}", logFile.getAbsolutePath(), e);
        }
        
        return errors;
    }
    
    /**
     * Checks if a log line contains a timestamp that is after the specified time.
     * 
     * @param line The log line to check
     * @param checkTime The time to compare against
     * @return true if the line has a timestamp after checkTime, false otherwise
     */
    private boolean containsTimestampAfter(String line, LocalDateTime checkTime) {
        // Simple implementation - assumes ISO format timestamps in logs
        // This should be adapted to match your actual log timestamp format
        try {
            Pattern timestampPattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
            Matcher matcher = timestampPattern.matcher(line);
            
            if (matcher.find()) {
                String timestamp = matcher.group();
                LocalDateTime lineTime = LocalDateTime.parse(timestamp, 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                return lineTime.isAfter(checkTime);
            }
        } catch (Exception e) {
            // If we can't parse the timestamp, assume it's relevant
            logger.debug("Could not parse timestamp from line: {}", line);
        }
        
        // Default to including the line if we can't determine its time
        return true;
    }
    
    /**
     * Sends an email alert with the critical errors found.
     * 
     * @param errors List of error messages to include in the email
     */
    private void sendAlertEmail(List<String> errors) {
        if (!emailEnabled) {
            logger.info("Email alerts are disabled. Skipping email notification.");
            return;
        }
        
        if (mailSender == null) {
            logger.warn("JavaMailSender is not configured. Cannot send email alerts.");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(emailTo);
            message.setSubject(emailSubject + " - " + 
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            StringBuilder body = new StringBuilder();
            body.append("Critical errors were detected in the ATDAID Framework logs:\n\n");
            
            int count = 0;
            for (String error : errors) {
                if (count++ > 10) {
                    body.append("\n... and ").append(errors.size() - 10)
                        .append(" more errors (see logs for details)");
                    break;
                }
                body.append("---ERROR ").append(count).append("---\n")
                    .append(error).append("\n\n");
            }
            
            message.setText(body.toString());
            mailSender.send(message);
            
            logger.info("Sent email alert with {} critical errors", errors.size());
        } catch (Exception e) {
            logger.error("Failed to send email alert", e);
        }
    }
    
    /**
     * Manually trigger a log check and alert if errors are found.
     * This can be called from other components when immediate checking is needed.
     * 
     * @return Number of critical errors found
     */
    public int manualCheckAndAlert() {
        if (!monitoringEnabled) {
            logger.info("Log monitoring is disabled. Manual check skipped.");
            return 0;
        }
        
        logger.info("Manual log check triggered at {}", 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        try {
            List<String> criticalErrors = findCriticalErrors();
            
            if (!criticalErrors.isEmpty()) {
                logger.warn("Manual check found {} critical errors in logs", criticalErrors.size());
                sendAlertEmail(criticalErrors);
            } else {
                logger.info("Manual check found no critical errors in logs");
            }
            
            return criticalErrors.size();
        } catch (Exception e) {
            logger.error("Error during manual log check", e);
            return 0;
        } finally {
            lastCheckTime = LocalDateTime.now();
        }
    }
}
