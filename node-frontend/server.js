/**
 * Express.js server with intentional security vulnerabilities for GHAS Workshop.
 * DO NOT USE IN PRODUCTION - This code contains deliberate security flaws.
 */

const express = require('express');
const path = require('path');
const fs = require('fs');
const { exec } = require('child_process');

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// In-memory user store for demo
const users = [
    { id: 1, username: 'admin', email: 'admin@example.com', role: 'admin' },
    { id: 2, username: 'user', email: 'user@example.com', role: 'user' }
];

// VULNERABILITY: Cross-Site Scripting (XSS) - Reflected
// User input is directly rendered in HTML without sanitization
app.get('/search', (req, res) => {
    const query = req.query.q || '';
    // VULNERABLE: User input directly in HTML response
    const html = `
        <html>
            <head><title>Search Results</title></head>
            <body>
                <h1>Search Results for: ${query}</h1>
                <p>No results found for your search.</p>
                <a href="/search">Try another search</a>
            </body>
        </html>
    `;
    res.send(html);
});

// VULNERABILITY: Cross-Site Scripting (XSS) - Stored simulation
app.post('/api/comments', (req, res) => {
    const { comment, author } = req.body;
    // VULNERABLE: Storing unsanitized user input
    const storedComment = {
        id: Date.now(),
        comment: comment,  // No sanitization
        author: author,    // No sanitization
        timestamp: new Date().toISOString()
    };
    // In a real app, this would be stored in a database
    res.json({
        message: 'Comment added',
        // VULNERABLE: Returning unsanitized data
        html: `<div class="comment"><strong>${author}</strong>: ${comment}</div>`
    });
});

// VULNERABILITY: Prototype Pollution
// Merging user-controlled objects without validation
app.post('/api/settings', (req, res) => {
    const userSettings = req.body;
    const defaultSettings = {
        theme: 'light',
        notifications: true,
        language: 'en'
    };
    
    // VULNERABLE: Object merge without prototype pollution protection
    function merge(target, source) {
        for (let key in source) {
            if (typeof source[key] === 'object' && source[key] !== null) {
                if (!target[key]) target[key] = {};
                merge(target[key], source[key]);
            } else {
                target[key] = source[key];
            }
        }
        return target;
    }
    
    const mergedSettings = merge(defaultSettings, userSettings);
    res.json({ settings: mergedSettings });
});

// VULNERABILITY: Path Traversal
app.get('/api/download', (req, res) => {
    const filename = req.query.file;
    // VULNERABLE: No validation of file path
    const filePath = path.join(__dirname, 'uploads', filename);
    
    fs.readFile(filePath, (err, data) => {
        if (err) {
            return res.status(404).json({ error: 'File not found' });
        }
        res.send(data);
    });
});

// VULNERABILITY: Command Injection
app.get('/api/lookup', (req, res) => {
    const domain = req.query.domain;
    // VULNERABLE: User input directly in shell command
    exec(`nslookup ${domain}`, (error, stdout, stderr) => {
        if (error) {
            return res.status(500).json({ error: stderr });
        }
        res.json({ result: stdout });
    });
});

// VULNERABILITY: Open Redirect
app.get('/redirect', (req, res) => {
    const url = req.query.url;
    // VULNERABLE: Redirecting to user-controlled URL without validation
    res.redirect(url);
});

// VULNERABILITY: Information Exposure through Error Messages
app.get('/api/user/:id', (req, res) => {
    try {
        const userId = parseInt(req.params.id);
        const user = users.find(u => u.id === userId);
        
        if (!user) {
            // VULNERABLE: Detailed error message exposes internal structure
            throw new Error(`User with ID ${userId} not found in database table 'users' at /var/lib/mysql/workshop/users.ibd`);
        }
        res.json(user);
    } catch (error) {
        // VULNERABLE: Exposing full error stack trace
        res.status(500).json({ 
            error: error.message,
            stack: error.stack,
            query: req.query,
            params: req.params
        });
    }
});

// VULNERABILITY: Missing Rate Limiting on sensitive endpoint
app.post('/api/login', (req, res) => {
    const { username, password } = req.body;
    // No rate limiting - vulnerable to brute force
    // In a real app, this would check credentials
    if (username === 'admin' && password === 'admin123') {
        res.json({ token: 'fake-jwt-token', user: { username, role: 'admin' } });
    } else {
        res.status(401).json({ error: 'Invalid credentials' });
    }
});

// VULNERABILITY: Insecure Direct Object Reference (IDOR)
app.get('/api/documents/:docId', (req, res) => {
    const docId = req.params.docId;
    // VULNERABLE: No authorization check - any user can access any document
    // Should verify the requesting user has access to this document
    res.json({ 
        id: docId, 
        title: 'Confidential Document',
        content: 'Sensitive business information...'
    });
});

// VULNERABILITY: JWT Algorithm Confusion
app.post('/api/verify-token', (req, res) => {
    const jwt = require('jsonwebtoken');
    const token = req.body.token;
    try {
        // VULNERABLE: Accepts 'none' algorithm, allowing token forgery
        const decoded = jwt.verify(token, 'secret-key', { algorithms: ['HS256', 'none'] });
        res.json({ valid: true, decoded });
    } catch (error) {
        res.status(401).json({ valid: false, error: error.message });
    }
});

// VULNERABILITY: Insecure CORS Configuration
app.use('/api/sensitive', (req, res, next) => {
    // VULNERABLE: Reflects any origin and allows credentials
    res.setHeader('Access-Control-Allow-Origin', req.headers.origin || '*');
    res.setHeader('Access-Control-Allow-Credentials', 'true');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE');
    next();
});

app.get('/api/sensitive/data', (req, res) => {
    res.json({ secret: 'This should not be accessible cross-origin' });
});

// Safe endpoint for comparison
app.get('/api/safe/search', (req, res) => {
    const query = req.query.q || '';
    // SAFE: Using proper encoding
    const sanitizedQuery = query
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#x27;');
    
    const html = `
        <html>
            <head><title>Search Results</title></head>
            <body>
                <h1>Search Results for: ${sanitizedQuery}</h1>
                <p>No results found for your search.</p>
            </body>
        </html>
    `;
    res.send(html);
});

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({ status: 'healthy', timestamp: new Date().toISOString() });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

module.exports = app;
