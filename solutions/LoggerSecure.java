/**
 * Secure Logger implementation for GHAS Workshop.
 * Demonstrates proper logging without exposing sensitive data.
 */
package com.workshop.ghas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LoggerSecure {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Patterns for detecting sensitive data
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?i)(password|passwd|pwd)\\s*[=:]\\s*\\S+");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("(?i)(token|apikey|api_key|secret)\\s*[=:]\\s*\\S+");
    
    /**
     * SECURE: Log user login without password
     * Never log credentials - only log the event and username
     */
    public void logUserLogin(String username, String password, String ipAddress) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // SECURE: Only log non-sensitive fields
        System.out.println("[" + timestamp + "] LOGIN ATTEMPT - User: " + username + ", IP: " + ipAddress);
        // Password is intentionally not logged
    }
    
    /**
     * SECURE: Log API request without token
     * Mask or omit sensitive headers and tokens
     */
    public void logApiRequest(String endpoint, String apiToken, String requestBody) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // SECURE: Mask the token, only show it exists
        String maskedToken = apiToken != null ? "[REDACTED]" : "[NONE]";
        
        // SECURE: Sanitize request body to remove any embedded secrets
        String sanitizedBody = sanitizeLogMessage(requestBody);
        
        System.out.println("[" + timestamp + "] API REQUEST - Endpoint: " + endpoint + ", Token: " + maskedToken);
        System.out.println("[" + timestamp + "] Request Body: " + sanitizedBody);
    }
    
    /**
     * SECURE: Log authentication result without secrets
     */
    public void logAuthentication(String userId, String secretKey, boolean success) {
        String timestamp = LocalDateTime.now().format(formatter);
        String status = success ? "SUCCESS" : "FAILED";
        
        // SECURE: Never log the secret key
        System.out.println("[" + timestamp + "] AUTH " + status + " - User: " + userId);
    }
    
    /**
     * SECURE: Log database connection without credentials
     */
    public void logDatabaseConnection(String host, int port, String username, String password) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // SECURE: Only log host and port, never credentials
        System.out.println("[" + timestamp + "] DB CONNECTION - Host: " + host + ":" + port + ", User: " + username);
    }
    
    /**
     * SECURE: Log payment without exposing card details
     * Use proper masking for PCI compliance
     */
    public void logPayment(String transactionId, String cardNumber, double amount) {
        String timestamp = LocalDateTime.now().format(formatter);
        
        // SECURE: Mask card number, only show last 4 digits
        String maskedCard = maskCardNumber(cardNumber);
        
        System.out.println("[" + timestamp + "] PAYMENT - Transaction: " + transactionId + 
                          ", Card: " + maskedCard + ", Amount: $" + amount);
    }
    
    /**
     * SECURE: Generic log method with automatic sanitization
     */
    public void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String sanitizedMessage = sanitizeLogMessage(message);
        System.out.println("[" + timestamp + "] [" + level + "] " + sanitizedMessage);
    }
    
    /**
     * Helper: Mask credit card number for PCI compliance
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "[INVALID]";
        }
        // Show only last 4 digits
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "****-****-****-" + lastFour;
    }
    
    /**
     * Helper: Remove sensitive data from log messages
     */
    private String sanitizeLogMessage(String message) {
        if (message == null) return "";
        
        String sanitized = message;
        
        // Remove passwords
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED]");
        
        // Remove tokens
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED]");
        
        return sanitized;
    }
    
    public static void main(String[] args) {
        LoggerSecure logger = new LoggerSecure();
        
        // Example: These will all log safely without exposing secrets
        logger.logUserLogin("john_doe", "secret123", "192.168.1.100");
        logger.logApiRequest("/api/users", "sk_live_abc123", "{\"name\":\"test\"}");
        logger.logAuthentication("user123", "super_secret", true);
        logger.logPayment("TXN-001", "4111111111111111", 99.99);
    }
}
