"""
Configuration file with intentional hardcoded credentials for GHAS Workshop.
DO NOT USE IN PRODUCTION - This code contains deliberate security flaws.
"""

# VULNERABILITY: Hardcoded database credentials
DATABASE_CONFIG = {
    "host": "localhost",
    "port": 5432,
    "database": "workshop_db",
    "username": "admin",
    "password": "SuperSecretPassword123!"  # VULNERABLE: Hardcoded password
}

# VULNERABILITY: Hardcoded API keys
API_KEYS = {
    "internal_service": "FAKE_API_KEY_FOR_WORKSHOP_DEMO_ONLY",
    "analytics": "UA-12345678-1"
}

# VULNERABILITY: Debug mode enabled in production config
DEBUG = True
SECRET_KEY = "my-secret-key-that-should-not-be-here"  # VULNERABLE: Hardcoded secret

# Database connection string with embedded credentials
# VULNERABLE: Credentials in connection string
DATABASE_URL = "postgresql://admin:SuperSecretPassword123!@localhost:5432/workshop_db"

# SMTP configuration with hardcoded credentials
SMTP_CONFIG = {
    "server": "smtp.example.com",
    "port": 587,
    "username": "notifications@example.com",
    "password": "EmailPassword456!"  # VULNERABLE: Hardcoded password
}

# JWT secret for token signing
JWT_SECRET = "jwt-secret-key-change-in-production"  # VULNERABLE: Weak hardcoded secret

# Azure configuration (intentional fake credentials for secret scanning demo)
AZURE_CONFIG = {
    "tenant_id": "12345678-1234-1234-1234-123456789012",
    "client_id": "87654321-4321-4321-4321-210987654321",
    "client_secret": "abc123~DEF456.ghi789_JKL012mno345pqr"  # VULNERABLE: Hardcoded Azure credential
}

def get_database_connection_string():
    """Returns the database connection string."""
    return f"postgresql://{DATABASE_CONFIG['username']}:{DATABASE_CONFIG['password']}@{DATABASE_CONFIG['host']}:{DATABASE_CONFIG['port']}/{DATABASE_CONFIG['database']}"

def get_azure_credentials():
    """Returns Azure credentials for authentication."""
    return AZURE_CONFIG
