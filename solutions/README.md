# Workshop Solutions

This directory contains the secure versions of the vulnerable code used in the workshop.

## Index

| File | Language | Vulnerability Fixed |
|------|----------|---------------------|
| `AppSecure.java` | Java | **Log Injection (CWE-117)** & **SQL Injection** |
| `LoggerSecure.java` | Java | **Sensitive Data Exposure** (Redaction logic) |
| `app_secure.py` | Python | **SQL Injection** (Parameterized queries) |
| `config_secure.py` | Python | **Hardcoded Secrets** (Environment variables) |
| `server_secure.js` | Node.js | **XSS (Cross-Site Scripting)** (Input sanitization) |
| `security-report.sh` | Bash | Reference script for API automation |

## Usage

Use these files to verify your fixes or to demonstrate the correct implementation during the workshop.
