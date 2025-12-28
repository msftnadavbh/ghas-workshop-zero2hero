"""
Flask API with intentional security vulnerabilities for GHAS Workshop.
DO NOT USE IN PRODUCTION - This code contains deliberate security flaws.
"""

from flask import Flask, request, jsonify, render_template_string
import sqlite3
import os
import subprocess

app = Flask(__name__)

# Database setup
def get_db_connection():
    conn = sqlite3.connect('database.db')
    conn.row_factory = sqlite3.Row
    return conn

def init_db():
    conn = get_db_connection()
    conn.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY,
            username TEXT NOT NULL,
            email TEXT NOT NULL,
            password TEXT NOT NULL
        )
    ''')
    conn.execute('''
        CREATE TABLE IF NOT EXISTS products (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            price REAL NOT NULL,
            description TEXT
        )
    ''')
    conn.commit()
    conn.close()

# VULNERABILITY: SQL Injection
# This endpoint is vulnerable because user input is directly concatenated into the SQL query
@app.route('/api/users/<user_id>')
def get_user(user_id):
    conn = get_db_connection()
    try:
        # VULNERABLE: Direct string concatenation in SQL query
        query = "SELECT * FROM users WHERE id = " + user_id
        cursor = conn.cursor()
        cursor.execute(query)
        user = cursor.fetchone()
        if user:
            return jsonify(dict(user))
        return jsonify({"error": "User not found"}), 404
    finally:
        conn.close()

# VULNERABILITY: SQL Injection in search
@app.route('/api/search')
def search_products():
    search_term = request.args.get('q', '')
    conn = get_db_connection()
    # VULNERABLE: User input directly in query
    query = "SELECT * FROM products WHERE name LIKE '%" + search_term + "%'"
    cursor = conn.cursor()
    cursor.execute(query)
    products = cursor.fetchall()
    conn.close()
    return jsonify([dict(p) for p in products])

# VULNERABILITY: Command Injection
# This endpoint allows arbitrary command execution
@app.route('/api/ping')
def ping_host():
    host = request.args.get('host', 'localhost')
    # VULNERABLE: User input passed directly to shell command
    result = subprocess.check_output("ping -c 1 " + host, shell=True)
    return result.decode()

# VULNERABILITY: Path Traversal
# This endpoint allows reading arbitrary files
@app.route('/api/files/<path:filename>')
def get_file(filename):
    # VULNERABLE: No validation of filename, allows ../../../etc/passwd
    base_path = "/app/uploads/"
    file_path = base_path + filename
    try:
        with open(file_path, 'r') as f:
            return f.read()
    except FileNotFoundError:
        return jsonify({"error": "File not found"}), 404

# VULNERABILITY: Server-Side Template Injection (SSTI)
@app.route('/api/welcome')
def welcome():
    name = request.args.get('name', 'Guest')
    # VULNERABLE: User input directly in template
    template = f"<h1>Welcome, {name}!</h1>"
    return render_template_string(template)

# VULNERABILITY: Server-Side Request Forgery (SSRF)
@app.route('/api/fetch')
def fetch_url():
    import requests
    url = request.args.get('url', '')
    # VULNERABLE: Fetching arbitrary URLs allows access to internal services
    # Attacker could use: ?url=http://169.254.169.254/latest/meta-data/
    try:
        response = requests.get(url, timeout=5)
        return response.text
    except Exception as e:
        return jsonify({"error": str(e)}), 400

# VULNERABILITY: Insecure Deserialization
@app.route('/api/import', methods=['POST'])
def import_data():
    import pickle
    import base64
    data = request.form.get('data', '')
    # VULNERABLE: Deserializing untrusted data can lead to RCE
    try:
        obj = pickle.loads(base64.b64decode(data))
        return jsonify({"imported": str(obj)})
    except Exception as e:
        return jsonify({"error": str(e)}), 400

# Safe endpoint for comparison
@app.route('/api/users/safe/<int:user_id>')
def get_user_safe(user_id):
    conn = get_db_connection()
    # SAFE: Using parameterized query
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))
    user = cursor.fetchone()
    conn.close()
    if user:
        # Don't return password in response
        return jsonify({
            "id": user["id"],
            "username": user["username"],
            "email": user["email"]
        })
    return jsonify({"error": "User not found"}), 404

@app.route('/health')
def health():
    return jsonify({"status": "healthy"})

if __name__ == '__main__':
    init_db()
    app.run(host='0.0.0.0', port=5000, debug=True)
