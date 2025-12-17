# Custom CodeQL Queries

This folder contains custom CodeQL queries for the workshop.

## Files

| File | Description |
|------|-------------|
| `sensitive-logging.ql` | **Starter template** - Complete this in Phase 6 |
| `sensitive-logging.ql.solution` | Reference solution |

## Phase 6 Exercise

Your task is to complete `sensitive-logging.ql` to detect when sensitive variables (passwords, tokens, secrets) are logged.

### Steps

1. Open `sensitive-logging.ql`
2. Implement `isLoggingMethod()` predicate
3. Implement `isSensitiveVariableName()` predicate
4. Complete the main query logic
5. Compare with the solution when done

### Testing Your Query

To test locally (requires CodeQL CLI):

```bash
# Install CodeQL CLI
gh extension install github/gh-codeql

# Create a database
codeql database create java-db --language=java --source-root=java-backend

# Run your query
codeql query run queries/sensitive-logging.ql --database=java-db
```

### Expected Results

Your query should find issues in `Logger.java`:
- `logUserLogin()` - logs password
- `logApiRequest()` - logs apiToken
- `logAuthentication()` - logs secretKey
- `logDatabaseConnection()` - logs password
- `logPayment()` - logs cardNumber

## Resources

- [CodeQL documentation](https://codeql.github.com/docs/)
- [CodeQL for Java](https://codeql.github.com/docs/codeql-language-guides/codeql-for-java/)
- [Writing queries](https://codeql.github.com/docs/writing-codeql-queries/)
