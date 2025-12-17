/**
 * Logger utility with intentional security vulnerability for GHAS Workshop.
 * DO NOT USE IN PRODUCTION - This code contains deliberate security flaws.
 * 
 * This class is specifically designed for the Phase 6 custom CodeQL query exercise.
 */
package com.workshop.ghas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * VULNERABILITY: Logging sensitive data
     * This method logs sensitive information that should never appear in logs
     */
    public void logUserLogin(String username, String password, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // VULNERABLE: Logging password in plain text
        System.out.println("[" + timestamp + "] LOGIN ATTEMPT - User: " + username + ", Password: " + password + ", IP: " + ipAddress);
    }
    
    /**
     * VULNERABILITY: Logging API tokens
     */
    public void logApiRequest(String endpoint, String apiToken, String requestBody) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // VULNERABLE: Logging API token
        System.out.println("[" + timestamp + "] API REQUEST - Endpoint: " + endpoint + ", Token: " + apiToken);
        System.out.println("[" + timestamp + "] Request Body: " + requestBody);
    }
    
    /**
     * VULNERABILITY: Logging credentials during authentication
     */
    public void logAuthentication(String userId, String secretKey, boolean success) {
        String timestamp = LocalDateTime.now().format(formatter);
        String status = success ? "SUCCESS" : "FAILED";
        
        // VULNERABLE: Logging secret key
        System.out.println("[" + timestamp + "] AUTH " + status + " - User: " + userId + ", SecretKey: " + secretKey);
    }
    
    /**
     * VULNERABILITY: Logging database connection with credentials
     */
    public void logDatabaseConnection(String host, String database, String username, String dbPassword) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // VULNERABLE: Logging database password
        System.out.println("[" + timestamp + "] DB CONNECTION - Host: " + host + ", DB: " + database);
        System.out.println("[" + timestamp + "] Credentials - User: " + username + ", Password: " + dbPassword);
    }
    
    /**
     * VULNERABILITY: Logging payment information
     */
    public void logPayment(String orderId, String creditCardNumber, String cvv, double amount) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // VULNERABLE: Logging credit card details
        System.out.println("[" + timestamp + "] PAYMENT - Order: " + orderId + ", Amount: $" + amount);
        System.out.println("[" + timestamp + "] Card: " + creditCardNumber + ", CVV: " + cvv);
    }
    
    /**
     * VULNERABILITY: Logging session tokens
     */
    public void logSessionCreation(String userId, String sessionToken, String refreshToken) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // VULNERABLE: Logging tokens
        System.out.println("[" + timestamp + "] SESSION CREATED - User: " + userId);
        System.out.println("[" + timestamp + "] Session Token: " + sessionToken);
        System.out.println("[" + timestamp + "] Refresh Token: " + refreshToken);
    }
    
    /**
     * Safe implementation for comparison
     */
    public void logUserLoginSafe(String username, String ipAddress, boolean success) {
        String timestamp = LocalDateTime.now().format(formatter);
        String status = success ? "SUCCESS" : "FAILED";
        
        // SAFE: Not logging sensitive data, only logging necessary information
        System.out.println("[" + timestamp + "] LOGIN " + status + " - User: " + username + ", IP: " + ipAddress);
    }
    
    /**
     * Safe implementation - masking sensitive data
     */
    public void logApiRequestSafe(String endpoint, String requestId) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // SAFE: Logging request ID instead of token
        System.out.println("[" + timestamp + "] API REQUEST - Endpoint: " + endpoint + ", RequestID: " + requestId);
    }
    
    public void info(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] INFO - " + message);
    }
    
    public void error(String message, Exception e) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] ERROR - " + message);
        if (e != null) {
            System.out.println("[" + timestamp + "] Exception: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }
    
    public void debug(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println("[" + timestamp + "] DEBUG - " + message);
    }
}
