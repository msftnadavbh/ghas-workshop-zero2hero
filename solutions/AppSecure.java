/**
 * Secure implementations of App.java for GHAS Workshop.
 * These demonstrate the fixes for each vulnerability.
 */
package com.workshop.ghas;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;

public class AppSecure {
    
    // SECURE: Load credentials from environment variables
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    
    /**
     * SECURE: SQL Injection Prevention
     * Use PreparedStatement with parameterized queries
     */
    public String getUserById(String userId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // SECURE: Parameterized query prevents SQL injection
            String query = "SELECT id, username, email FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    StringBuilder result = new StringBuilder();
                    while (rs.next()) {
                        result.append(rs.getString("username"));
                        result.append(" - ");
                        result.append(rs.getString("email"));
                    }
                    return result.toString();
                }
            }
        }
    }
    
    /**
     * SECURE: SSRF Prevention
     * Validate URL against allowlist of permitted hosts
     */
    public String fetchUrl(String urlString) throws IOException {
        // SECURE: Allowlist of permitted external hosts
        Set<String> allowedHosts = Set.of(
            "api.github.com",
            "api.example.com"
        );
        
        URL url = new URL(urlString);
        String host = url.getHost().toLowerCase();
        
        // SECURE: Block internal IP ranges
        if (isInternalAddress(host)) {
            throw new SecurityException("Access to internal addresses is not permitted");
        }
        
        // SECURE: Check against allowlist
        if (!allowedHosts.contains(host)) {
            throw new SecurityException("Host not in allowlist: " + host);
        }
        
        // SECURE: Only allow HTTPS
        if (!"https".equals(url.getProtocol())) {
            throw new SecurityException("Only HTTPS is permitted");
        }
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
    
    private boolean isInternalAddress(String host) {
        // Block localhost, private IP ranges, and metadata endpoints
        return host.equals("localhost") ||
               host.equals("127.0.0.1") ||
               host.startsWith("192.168.") ||
               host.startsWith("10.") ||
               host.startsWith("172.16.") ||
               host.equals("169.254.169.254") ||
               host.endsWith(".internal");
    }
    
    /**
     * SECURE: XXE Prevention
     * Disable external entities and DTDs
     */
    public String parseXml(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        // SECURE: Disable all external entity processing
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
        
        NodeList nodes = doc.getElementsByTagName("data");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }
    
    /**
     * SECURE: Path Traversal Prevention
     * Validate and canonicalize file paths
     */
    public String readFile(String filename) throws IOException {
        // SECURE: Only allow alphanumeric filenames with specific extensions
        Pattern safeFilename = Pattern.compile("^[a-zA-Z0-9_-]+\\.(txt|json|xml)$");
        if (!safeFilename.matcher(filename).matches()) {
            throw new SecurityException("Invalid filename");
        }
        
        File baseDir = new File("/app/data/").getCanonicalFile();
        File requestedFile = new File(baseDir, filename).getCanonicalFile();
        
        // SECURE: Verify the canonical path is within base directory
        if (!requestedFile.getPath().startsWith(baseDir.getPath())) {
            throw new SecurityException("Path traversal detected");
        }
        
        if (!requestedFile.exists()) {
            throw new FileNotFoundException("File not found");
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(requestedFile))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }
    
    /**
     * SECURE: Command Injection Prevention
     * Use ProcessBuilder with argument array, validate input
     */
    public String executeCommand(String host) throws IOException {
        // SECURE: Validate host format (hostname or IP only)
        Pattern validHost = Pattern.compile("^[a-zA-Z0-9.-]+$");
        if (!validHost.matcher(host).matches() || host.length() > 253) {
            throw new SecurityException("Invalid host format");
        }
        
        // SECURE: Use ProcessBuilder with separate arguments (no shell interpretation)
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "4", host);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        }
    }
    
    /**
     * SECURE: Deserialization
     * Use allowlist-based filtering or avoid Java serialization entirely
     */
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        // SECURE: Use JSON or other safe formats instead of Java serialization
        // For this example, we show how to use an ObjectInputFilter
        
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) 
                    throws IOException, ClassNotFoundException {
                // SECURE: Only allow specific safe classes
                Set<String> allowedClasses = Set.of(
                    "java.lang.String",
                    "java.lang.Integer",
                    "java.util.ArrayList",
                    "java.util.HashMap"
                );
                
                if (!allowedClasses.contains(desc.getName())) {
                    throw new SecurityException("Class not allowed: " + desc.getName());
                }
                return super.resolveClass(desc);
            }
        };
        
        return ois.readObject();
    }
    
    public static void main(String[] args) {
        System.out.println("GHAS Workshop - Secure Java Backend");
        System.out.println("This demonstrates secure implementations.");
    }
}
