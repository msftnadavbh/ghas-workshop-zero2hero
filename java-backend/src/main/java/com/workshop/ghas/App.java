/**
 * Main application with intentional security vulnerabilities for GHAS Workshop.
 * DO NOT USE IN PRODUCTION - This code contains deliberate security flaws.
 */
package com.workshop.ghas;

import java.io.*;
import java.net.*;
import java.sql.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;

public class App {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/workshop";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "rootpassword123"; // VULNERABLE: Hardcoded credential
    
    /**
     * VULNERABILITY: SQL Injection
     * User input is directly concatenated into SQL query
     */
    public String getUserById(String userId) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
        
            // VULNERABLE: Direct string concatenation in SQL query
            String query = "SELECT * FROM users WHERE id = '" + userId + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append(rs.getString("username"));
                result.append(" - ");
                result.append(rs.getString("email"));
            }
            return result.toString();
        }
    }
    
    /**
     * VULNERABILITY: Server-Side Request Forgery (SSRF)
     * User-controlled URL is fetched without validation
     */
    public String fetchUrl(String urlString) throws IOException {
        // VULNERABLE: No validation of URL - allows access to internal resources
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream())
        );
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    /**
     * VULNERABILITY: XML External Entity (XXE) Injection
     * XML parser is configured to process external entities
     */
    public String parseXml(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // VULNERABLE: External entities enabled (default in older versions)
        // factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); // This line is commented out
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
        
        NodeList nodes = doc.getElementsByTagName("data");
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }
    
    /**
     * VULNERABILITY: Path Traversal
     * File path is constructed from user input without validation
     */
    public String readFile(String filename) throws IOException {
        // VULNERABLE: No validation allows ../../../etc/passwd
        String basePath = "/app/data/";
        String filePath = basePath + filename;
        
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        
        return content.toString();
    }
    
    /**
     * VULNERABILITY: Command Injection
     * User input is passed directly to shell command
     */
    public String executeCommand(String host) throws IOException {
        // VULNERABLE: User input directly in command
        String command = "ping -c 4 " + host;
        Process process = Runtime.getRuntime().exec(command);
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
        );
        
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        
        return output.toString();
    }
    
    /**
     * VULNERABILITY: Insecure Deserialization
     * Deserializing untrusted data without validation
     */
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        // VULNERABLE: Deserializing untrusted data
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
    
    /**
     * Safe implementation for comparison - SQL Injection prevention
     */
    public String getUserByIdSafe(String userId) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        // SAFE: Using PreparedStatement with parameterized query
        String query = "SELECT * FROM users WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, userId);
        ResultSet rs = pstmt.executeQuery();
        
        StringBuilder result = new StringBuilder();
        while (rs.next()) {
            result.append(rs.getString("username"));
            result.append(" - ");
            result.append(rs.getString("email"));
        }
        
        conn.close();
        return result.toString();
    }
    
    public static void main(String[] args) {
        System.out.println("GHAS Workshop - Java Backend");
        System.out.println("This application contains intentional vulnerabilities for training.");
    }
}
