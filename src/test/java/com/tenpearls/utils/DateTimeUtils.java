package com.tenpearls.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for date and time operations.
 */
public class DateTimeUtils {
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    
    /**
     * Gets the current date as a formatted string.
     * 
     * @return The current date in yyyy-MM-dd format
     */
    public static String getCurrentDate() {
        return formatDate(new Date(), DATE_FORMAT);
    }
    
    /**
     * Gets the current time as a formatted string.
     * 
     * @return The current time in HH:mm:ss format
     */
    public static String getCurrentTime() {
        return formatDate(new Date(), TIME_FORMAT);
    }
    
    /**
     * Gets the current date and time as a formatted string.
     * 
     * @return The current date and time in yyyy-MM-dd_HH-mm-ss format
     */
    public static String getCurrentDateTime() {
        return formatDate(new Date(), DATE_TIME_FORMAT);
    }
    
    /**
     * Formats a date using the specified pattern.
     * 
     * @param date The date to format
     * @param pattern The format pattern
     * @return The formatted date string
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
} 