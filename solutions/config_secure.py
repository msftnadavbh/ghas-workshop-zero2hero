# Solution: Secure Configuration File
# This demonstrates proper configuration management

import os
from dataclasses import dataclass
from typing import Optional

@dataclass
class DatabaseConfig:
    """Database configuration loaded from environment variables."""
    host: str
    port: int
    database: str
    username: str
    password: str
    
    @classmethod
    def from_env(cls) -> 'DatabaseConfig':
        """Load database configuration from environment variables."""
        return cls(
            host=os.environ.get('DB_HOST', 'localhost'),
            port=int(os.environ.get('DB_PORT', '5432')),
            database=os.environ.get('DB_NAME', 'workshop_db'),
            username=os.environ.get('DB_USER', ''),
            password=os.environ.get('DB_PASSWORD', '')
        )
    
    @property
    def connection_string(self) -> str:
        """Generate connection string without exposing credentials in logs."""
        return f"postgresql://{self.username}:****@{self.host}:{self.port}/{self.database}"
    
    def get_connection_string_secure(self) -> str:
        """Get actual connection string for database driver."""
        if not self.username or not self.password:
            raise ValueError("Database credentials not configured")
        return f"postgresql://{self.username}:{self.password}@{self.host}:{self.port}/{self.database}"


@dataclass
class AzureConfig:
    """Azure configuration loaded from environment variables."""
    tenant_id: str
    client_id: str
    client_secret: str
    
    @classmethod
    def from_env(cls) -> 'AzureConfig':
        """Load Azure configuration from environment variables."""
        return cls(
            tenant_id=os.environ.get('AZURE_TENANT_ID', ''),
            client_id=os.environ.get('AZURE_CLIENT_ID', ''),
            client_secret=os.environ.get('AZURE_CLIENT_SECRET', '')
        )
    
    def is_configured(self) -> bool:
        """Check if Azure credentials are properly configured."""
        return bool(self.tenant_id and self.client_id and self.client_secret)


@dataclass
class AppConfig:
    """Main application configuration."""
    debug: bool
    secret_key: str
    database: DatabaseConfig
    azure: Optional[AzureConfig]
    
    @classmethod
    def from_env(cls) -> 'AppConfig':
        """Load all configuration from environment variables."""
        # SECURE: Debug mode defaults to False
        debug = os.environ.get('DEBUG', 'false').lower() == 'true'
        
        # SECURE: Secret key must be set in environment
        secret_key = os.environ.get('SECRET_KEY', '')
        if not secret_key:
            raise ValueError("SECRET_KEY environment variable must be set")
        
        return cls(
            debug=debug,
            secret_key=secret_key,
            database=DatabaseConfig.from_env(),
            azure=AzureConfig.from_env()
        )


def get_config() -> AppConfig:
    """Get application configuration.
    
    This function loads configuration from environment variables.
    In production, use a secrets manager like Azure Key Vault.
    """
    return AppConfig.from_env()


# Example .env file content (for documentation only):
"""
# .env.example - Copy to .env and fill in values
# NEVER commit .env to version control

# Application
DEBUG=false
SECRET_KEY=your-secure-random-key-here

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=workshop_db
DB_USER=dbuser
DB_PASSWORD=your-secure-password

# Azure (optional)
AZURE_TENANT_ID=your-tenant-id
AZURE_CLIENT_ID=your-client-id
AZURE_CLIENT_SECRET=your-client-secret
"""
