package com.tenpearls.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations.
 * Provides methods for common file manipulation tasks.
 */
public class FileUtils {
    
    /**
     * Reads the contents of a file as a string.
     * 
     * @param filePath The path to the file
     * @return The contents of the file as a string
     * @throws IOException If an I/O error occurs
     */
    public static String readFileAsString(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }
    
    /**
     * Reads the contents of a file as a list of strings, one per line.
     * 
     * @param filePath The path to the file
     * @return The contents of the file as a list of strings
     * @throws IOException If an I/O error occurs
     */
    public static List<String> readFileAsLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
    
    /**
     * Writes a string to a file.
     * 
     * @param content The content to write
     * @param filePath The path to the file
     * @throws IOException If an I/O error occurs
     */
    public static void writeStringToFile(String content, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }
    
    /**
     * Writes a list of strings to a file, one per line.
     * 
     * @param lines The lines to write
     * @param filePath The path to the file
     * @throws IOException If an I/O error occurs
     */
    public static void writeLinesToFile(List<String> lines, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        }
    }
    
    /**
     * Creates a directory if it doesn't exist.
     * 
     * @param directoryPath The path to the directory
     * @return true if the directory was created, false if it already exists
     * @throws IOException If an I/O error occurs
     */
    public static boolean createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            return true;
        }
        return false;
    }
    
    /**
     * Gets the file extension.
     * 
     * @param fileName The file name
     * @return The file extension, or an empty string if there is no extension
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * Gets the file name without extension.
     * 
     * @param fileName The file name
     * @return The file name without extension
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        
        return fileName.substring(0, lastDotIndex);
    }
    
    /**
     * Checks if a file exists.
     * 
     * @param filePath The path to the file
     * @return true if the file exists
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
} 