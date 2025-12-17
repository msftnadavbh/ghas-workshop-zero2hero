/**
 * Solution: Fixed Express.js Server with Security Best Practices
 * This file demonstrates the secure versions of the vulnerable endpoints.
 */

const express = require('express');
const path = require('path');
const fs = require('fs');
const { execFile } = require('child_process');

const app = express();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// SECURE: HTML encoding helper
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#x27;',
        '/': '&#x2F;'
    };
    return text.replace(/[&<>"'/]/g, char => map[char]);
}

// SECURE: XSS Prevention with proper encoding
app.get('/search', (req, res) => {
    const query = req.query.q || '';
    
    // SECURE: Escape user input before rendering in HTML
    const safeQuery = escapeHtml(query);
    
    const html = `
        <html>
            <head>
                <title>Search Results</title>
                <meta http-equiv="Content-Security-Policy" content="default-src 'self'">
            </head>
            <body>
                <h1>Search Results for: ${safeQuery}</h1>
                <p>No results found for your search.</p>
                <a href="/search">Try another search</a>
            </body>
        </html>
    `;
    res.send(html);
});

// SECURE: XSS Prevention in API responses
app.post('/api/comments', (req, res) => {
    const { comment, author } = req.body;
    
    // SECURE: Sanitize input before storing/returning
    const sanitizedComment = escapeHtml(comment || '');
    const sanitizedAuthor = escapeHtml(author || 'Anonymous');
    
    const storedComment = {
        id: Date.now(),
        comment: sanitizedComment,
        author: sanitizedAuthor,
        timestamp: new Date().toISOString()
    };
    
    res.json({
        message: 'Comment added',
        data: storedComment
    });
});

// SECURE: Prototype Pollution Prevention
app.post('/api/settings', (req, res) => {
    const userSettings = req.body;
    const defaultSettings = {
        theme: 'light',
        notifications: true,
        language: 'en'
    };
    
    // SECURE: Safe merge that prevents prototype pollution
    function safeMerge(target, source) {
        const allowedKeys = ['theme', 'notifications', 'language'];
        const result = { ...target };
        
        for (const key of allowedKeys) {
            if (source.hasOwnProperty(key) && typeof source[key] !== 'object') {
                result[key] = source[key];
            }
        }
        return result;
    }
    
    const mergedSettings = safeMerge(defaultSettings, userSettings);
    res.json({ settings: mergedSettings });
});

// SECURE: Path Traversal Prevention
app.get('/api/download', (req, res) => {
    const filename = req.query.file;
    
    if (!filename) {
        return res.status(400).json({ error: 'Filename required' });
    }
    
    // SECURE: Validate filename and resolve path safely
    const safeName = path.basename(filename); // Remove any path components
    const uploadsDir = path.resolve(__dirname, 'uploads');
    const filePath = path.join(uploadsDir, safeName);
    
    // SECURE: Verify the resolved path is within uploads directory
    if (!filePath.startsWith(uploadsDir)) {
        return res.status(403).json({ error: 'Access denied' });
    }
    
    fs.access(filePath, fs.constants.R_OK, (err) => {
        if (err) {
            return res.status(404).json({ error: 'File not found' });
        }
        res.sendFile(filePath);
    });
});

// SECURE: Command Injection Prevention
app.get('/api/lookup', (req, res) => {
    const domain = req.query.domain;
    
    // SECURE: Validate input
    if (!domain || !/^[a-zA-Z0-9][a-zA-Z0-9\-\.]*[a-zA-Z0-9]$/.test(domain)) {
        return res.status(400).json({ error: 'Invalid domain' });
    }
    
    // SECURE: Use execFile with arguments array instead of shell
    execFile('nslookup', [domain], { timeout: 5000 }, (error, stdout, stderr) => {
        if (error) {
            return res.status(500).json({ error: 'Lookup failed' });
        }
        res.json({ result: stdout });
    });
});

// SECURE: Open Redirect Prevention
app.get('/redirect', (req, res) => {
    const url = req.query.url;
    
    // SECURE: Whitelist allowed redirect domains
    const allowedDomains = ['example.com', 'trusted-site.com'];
    
    try {
        const parsedUrl = new URL(url);
        if (!allowedDomains.includes(parsedUrl.hostname)) {
            return res.status(400).json({ error: 'Redirect not allowed' });
        }
        res.redirect(url);
    } catch (e) {
        // SECURE: For relative URLs, only allow paths starting with /
        if (url && url.startsWith('/') && !url.startsWith('//')) {
            res.redirect(url);
        } else {
            res.status(400).json({ error: 'Invalid redirect URL' });
        }
    }
});

// SECURE: Error Handling without Information Exposure
app.get('/api/user/:id', (req, res) => {
    try {
        const userId = parseInt(req.params.id);
        
        if (isNaN(userId)) {
            return res.status(400).json({ error: 'Invalid user ID' });
        }
        
        // Simulated user lookup
        const users = [
            { id: 1, username: 'admin', email: 'admin@example.com', role: 'admin' },
            { id: 2, username: 'user', email: 'user@example.com', role: 'user' }
        ];
        
        const user = users.find(u => u.id === userId);
        
        if (!user) {
            // SECURE: Generic error message
            return res.status(404).json({ error: 'User not found' });
        }
        
        res.json(user);
    } catch (error) {
        // SECURE: Log error internally but return generic message
        console.error('User lookup error:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});

// Health check
app.get('/health', (req, res) => {
    res.json({ status: 'healthy', timestamp: new Date().toISOString() });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});

module.exports = app;
