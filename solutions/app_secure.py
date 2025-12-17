"""
Solution: Fixed Python API with Security Best Practices
This file demonstrates the secure versions of the vulnerable endpoints.
"""

from flask import Flask, request, jsonify
import sqlite3

app = Flask(__name__)

def get_db_connection():
    conn = sqlite3.connect('database.db')
    conn.row_factory = sqlite3.Row
    return conn

# SECURE: SQL Injection Prevention using Parameterized Queries
@app.route('/api/users/<user_id>')
def get_user_secure(user_id):
    conn = get_db_connection()
    cursor = conn.cursor()
    
    # SECURE: Using parameterized query with placeholder
    cursor.execute("SELECT id, username, email FROM users WHERE id = ?", (user_id,))
    user = cursor.fetchone()
    conn.close()
    
    if user:
        # SECURE: Not returning password field
        return jsonify({
            "id": user["id"],
            "username": user["username"],
            "email": user["email"]
        })
    return jsonify({"error": "User not found"}), 404


# SECURE: SQL Injection Prevention in Search
@app.route('/api/search')
def search_products_secure():
    search_term = request.args.get('q', '')
    conn = get_db_connection()
    cursor = conn.cursor()
    
    # SECURE: Using parameterized query with LIKE
    cursor.execute(
        "SELECT * FROM products WHERE name LIKE ?", 
        (f'%{search_term}%',)
    )
    products = cursor.fetchall()
    conn.close()
    
    return jsonify([dict(p) for p in products])


# SECURE: Command Injection Prevention
@app.route('/api/ping')
def ping_host_secure():
    import subprocess
    import re
    
    host = request.args.get('host', 'localhost')
    
    # SECURE: Validate input - only allow valid hostnames/IPs
    if not re.match(r'^[a-zA-Z0-9][a-zA-Z0-9\-\.]*$', host):
        return jsonify({"error": "Invalid hostname"}), 400
    
    # SECURE: Use list arguments instead of shell=True
    try:
        result = subprocess.check_output(
            ["ping", "-c", "1", host],
            timeout=5,
            stderr=subprocess.DEVNULL
        )
        return result.decode()
    except subprocess.CalledProcessError:
        return jsonify({"error": "Ping failed"}), 500
    except subprocess.TimeoutExpired:
        return jsonify({"error": "Ping timeout"}), 504


# SECURE: Path Traversal Prevention
@app.route('/api/files/<path:filename>')
def get_file_secure(filename):
    import os
    
    base_path = "/app/uploads/"
    
    # SECURE: Resolve the absolute path and verify it's within base_path
    requested_path = os.path.abspath(os.path.join(base_path, filename))
    
    if not requested_path.startswith(os.path.abspath(base_path)):
        return jsonify({"error": "Access denied"}), 403
    
    # SECURE: Check file exists and is a file (not directory)
    if not os.path.isfile(requested_path):
        return jsonify({"error": "File not found"}), 404
    
    try:
        with open(requested_path, 'r') as f:
            return f.read()
    except IOError:
        return jsonify({"error": "Unable to read file"}), 500


# SECURE: Server-Side Template Injection Prevention
@app.route('/api/welcome')
def welcome_secure():
    from markupsafe import escape
    
    name = request.args.get('name', 'Guest')
    
    # SECURE: Escape user input before rendering
    safe_name = escape(name)
    
    return f"<h1>Welcome, {safe_name}!</h1>"


@app.route('/health')
def health():
    return jsonify({"status": "healthy"})


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)  # SECURE: debug=False in production
