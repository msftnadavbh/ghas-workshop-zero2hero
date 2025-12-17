# Participant Guide

## GitHub Advanced Security Workshop

*Created by **Nadav Ben Haim***

Welcome to the hands-on GitHub Advanced Security workshop. This guide contains all exercises organized by phase. Work through each phase in order, as later phases build on earlier ones.

---

## Before You Begin

### Prerequisites Checklist

- [ ] GitHub account (free account works)
- [ ] GitHub CLI installed (`gh --version`)
- [ ] Git installed (`git --version`)
- [ ] Terminal access (bash, zsh, PowerShell)
- [ ] Authenticated with GitHub CLI (`gh auth login`)

### Create Your Workshop Repository

```bash
gh repo create ghas-workshop --template msftnadavbh/ghas-workshop-template --public --clone
cd ghas-workshop
```

**Your repository must be public** to access security features for free.

### Set Your Variables

Throughout this guide, replace `{owner}` with your GitHub username:

```bash
# Set your username for easy copy-paste
export OWNER=$(gh api user --jq '.login')
export REPO="ghas-workshop"
echo "Working with: $OWNER/$REPO"
```

---

## Phase 1: Discovering What is in Your Code (30 min)

### What You Will Learn

Every application depends on external libraries. Before you can secure your software, you need to know what components it contains. In this phase, you will explore your dependencies and generate a Software Bill of Materials (SBOM).

### What is a Software Bill of Materials?

A Software Bill of Materials is a complete list of all components, libraries, and dependencies in your application. Think of it like an ingredient list for software. It tells you exactly what went into building your application. Organizations use SBOMs for:

- **Compliance audits**: Proving what software you use
- **Vulnerability tracking**: Knowing what to update when issues are found
- **License management**: Understanding legal obligations
- **Incident response**: Quickly identifying affected systems

---

### Exercise 1.1: Explore Your Dependencies (10 min)

Your application uses dozens of external libraries. Let us see what they are.

**View Node.js dependencies:**
```bash
cat node-frontend/package.json | grep -A 20 '"dependencies"'
```

**View Python dependencies:**
```bash
cat python-api/requirements.txt
```

**View Java dependencies:**
```bash
cat java-backend/pom.xml | grep -A 2 '<dependency>' | head -30
```

These files show your direct dependencies. But each of these brings its own dependencies, called transitive dependencies.

---

### Exercise 1.2: Enable the Dependency Graph (5 min)

GitHub can analyze your dependencies and their dependencies automatically.

```bash
# Enable vulnerability alerts (this also enables the dependency graph)
gh api repos/$OWNER/$REPO/vulnerability-alerts -X PUT
```

View the dependency graph in your browser:
```bash
gh browse -- /network/dependencies
```

---

### Exercise 1.3: Generate Your SBOM (10 min)

Now generate a complete inventory of all components.

```bash
# Export SBOM in SPDX format
gh api repos/$OWNER/$REPO/dependency-graph/sbom > sbom.json

# View the structure
head -50 sbom.json

# Count total packages (including transitive dependencies)
cat sbom.json | jq '.sbom.packages | length'

# List package names and versions
cat sbom.json | jq -r '.sbom.packages[] | "\(.name) @ \(.versionInfo)"' | head -20
```

**What you discovered**: Your application likely has far more components than expected. A typical application can have hundreds of packages in its dependency tree.

---

### Exercise 1.4: Create Your Security Policy (5 min)

Every repository should have a security policy telling users how to report vulnerabilities.

```bash
# Copy the template
cp SECURITY.md.template SECURITY.md

# Replace placeholder with your email
sed -i 's/CONTACT_EMAIL/security@yourcompany.com/g' SECURITY.md
sed -i 's/REPO_NAME/ghas-workshop/g' SECURITY.md

# Commit the security policy
git add SECURITY.md
git commit -m "Add security policy"
git push
```

View your security policy:
```bash
gh browse -- /security/policy
```

---

### Phase 1 Checklist

- [ ] Explored dependencies in package.json, requirements.txt, and pom.xml
- [ ] Generated SBOM with total component count
- [ ] Created and committed SECURITY.md

---

## Phase 2: Finding Vulnerabilities in Your Code (35 min)

### What You Will Learn

Your code may contain security vulnerabilities that are not obvious during development. Code scanning uses static analysis to examine your source code and find dangerous patterns that could lead to security breaches.

### What is Static Analysis?

Static analysis reads your code and looks for dangerous patterns without running it. It can detect issues like:

- **SQL Injection**: User input passed directly to database queries
- **Cross-Site Scripting (XSS)**: User data displayed without sanitization
- **Path Traversal**: User input used to access file paths
- **Command Injection**: User input passed to shell commands

---

### Exercise 2.1: Enable Code Scanning (5 min)

Enable code scanning using the default setup:

```bash
# Enable default code scanning
gh api repos/$OWNER/$REPO/code-scanning/default-setup -X PATCH -f state=configured

# Verify it is enabled
gh api repos/$OWNER/$REPO/code-scanning/default-setup --jq '.state'
```

This triggers a GitHub Actions workflow. Watch its progress:
```bash
gh run list --limit 5
gh run watch
```

The scan takes 3-5 minutes to complete.

---

### Exercise 2.2: Examine the Vulnerable Code (10 min)

While the scan runs, look at the code it will find.

**SQL Injection in Python:**
```bash
# Find the vulnerable code
grep -n "SELECT.*+" python-api/app.py
```

Look at line 37. User input (`user_id`) is directly concatenated into the SQL query. An attacker could input `1 OR 1=1` to retrieve all users, or `1; DROP TABLE users;--` to delete data.

**XSS in JavaScript:**
```bash
grep -n "query}" node-frontend/server.js
```

Look at line 22. User input (`query`) is inserted directly into HTML without encoding. An attacker could input `<script>alert('hacked')</script>` to execute JavaScript in other users' browsers.

**SSRF in Java:**
```bash
grep -n "new URL" java-backend/src/main/java/com/workshop/ghas/App.java
```

Look at line 45. A user-provided URL is fetched without validation. An attacker could input `http://169.254.169.254/latest/meta-data/` to access cloud metadata.

---

### Exercise 2.3: Review the Scan Results (10 min)

Once the workflow completes, view the alerts:

```bash
# List all alerts
gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '.[] | {number: .number, rule: .rule.id, severity: .rule.security_severity_level, file: .most_recent_instance.location.path, line: .most_recent_instance.location.start_line}'

# Count by severity
gh api repos/$OWNER/$REPO/code-scanning/alerts --jq 'group_by(.rule.security_severity_level) | .[] | {severity: .[0].rule.security_severity_level, count: length}'

# Get details on alert #1
gh api repos/$OWNER/$REPO/code-scanning/alerts/1
```

View alerts in the browser:
```bash
gh browse -- /security/code-scanning
```

---

### Exercise 2.4: Fix a Vulnerability (10 min)

Fix the SQL injection vulnerability in Python.

**Option A: Use Copilot Autofix**

1. Open the alert in your browser: `gh browse -- /security/code-scanning/1`
2. Click "Generate fix" if available
3. Review the suggested change
4. Click "Commit fix"

**Option B: Manual fix**

```bash
# Create a branch
git checkout -b fix-sql-injection

# Edit python-api/app.py
# Change line 37 from:
#   query = "SELECT * FROM users WHERE id = " + user_id
# To:
#   query = "SELECT * FROM users WHERE id = ?"
# And change line 39 from:
#   cursor.execute(query)
# To:
#   cursor.execute(query, (user_id,))
```

Commit and create a pull request:
```bash
git add python-api/app.py
git commit -m "Fix SQL injection using parameterized queries"
git push -u origin fix-sql-injection
gh pr create --title "Fix SQL injection vulnerability" --body "Resolves code scanning alert #1 by using parameterized queries instead of string concatenation."
```

---

### Phase 2 Checklist

- [ ] Enabled code scanning
- [ ] Examined vulnerable code patterns
- [ ] Viewed scan results and alert details
- [ ] Fixed at least one vulnerability

---

## Phase 3: Preventing Secrets from Leaking (30 min)

### What You Will Learn

Accidentally committing passwords, API keys, or tokens is one of the most common causes of security breaches. In this phase, you will enable secret scanning to detect existing secrets and push protection to prevent new ones.

### Why Leaked Secrets Matter

When credentials are committed to a repository:
- They remain in git history even after deletion
- They may be copied to forks, backups, and mirrors
- Automated scanners constantly search public repos for secrets
- Rotating compromised credentials takes hours or days

Prevention is far more effective than remediation.

---

### Exercise 3.1: Enable Secret Scanning (5 min)

```bash
# Enable secret scanning and push protection
gh api repos/$OWNER/$REPO -X PATCH \
  --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"},"secret_scanning_push_protection":{"status":"enabled"}}}'

# Verify settings
gh api repos/$OWNER/$REPO --jq '{secret_scanning: .security_and_analysis.secret_scanning.status, push_protection: .security_and_analysis.secret_scanning_push_protection.status}'
```

Check for existing alerts:
```bash
gh api repos/$OWNER/$REPO/secret-scanning/alerts --jq '.[] | {number: .number, secret_type: .secret_type, state: .state}'
```

---

### Exercise 3.2: Examine Detected Secrets (5 min)

Your repository contains an intentional fake secret:

```bash
# Find the secret in the codebase
cat node-frontend/.env.example
```

View the alert details:
```bash
gh api repos/$OWNER/$REPO/secret-scanning/alerts/1 --jq '{type: .secret_type, file: .locations[0].path, state: .state}'
```

---

### Exercise 3.3: Experience Push Protection (10 min)

Try to commit a new secret and watch push protection block it.

```bash
# Create a file with a fake GitHub token
echo "GITHUB_TOKEN=ghp_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef12" > test-secret.txt

# Stage and commit
git add test-secret.txt
git commit -m "Add configuration"

# Attempt to push
git push
```

You should see a message explaining that the push was blocked because it contains a secret.

**Clean up:**
```bash
rm test-secret.txt
git reset HEAD~1
git status
```

---

### Exercise 3.4: Create a Custom Secret Pattern (10 min)

Organizations often have internal credentials with specific formats. You can create custom patterns to detect these.

**Via the browser:**
```bash
gh browse -- /settings/security_analysis
```

1. Scroll to "Custom patterns"
2. Click "New pattern"
3. Enter:
   - **Pattern name**: Workshop Internal Token
   - **Secret format**: `WORKSHOP-[A-Z0-9]{16}`
   - **Test string**: `WORKSHOP-ABC123XYZ789DEF0`
4. Click "Save and dry run"
5. Review results
6. Click "Publish pattern"

**Test your pattern:**
```bash
echo "API_KEY=WORKSHOP-ABCD1234EFGH5678" > custom-secret.txt
git add custom-secret.txt
git commit -m "Add config"
git push
# Should be blocked
```

**Clean up:**
```bash
rm custom-secret.txt
git reset HEAD~1
```

---

### Phase 3 Checklist

- [ ] Enabled secret scanning and push protection
- [ ] Viewed existing secret alerts
- [ ] Experienced push protection blocking a commit
- [ ] Created a custom secret pattern

---

## Phase 4: Automating Dependency Updates (35 min)

### What You Will Learn

When vulnerabilities are discovered in your dependencies, time matters. Dependabot automatically monitors your dependencies, alerts you to issues, and creates pull requests with fixes.

---

### Exercise 4.1: Enable Dependabot Alerts (5 min)

```bash
# Enable Dependabot alerts
gh api repos/$OWNER/$REPO/vulnerability-alerts -X PUT

# View existing alerts
gh api repos/$OWNER/$REPO/dependabot/alerts --jq '.[] | {number: .number, package: .security_vulnerability.package.name, severity: .security_vulnerability.severity}' | head -20

# Count by severity
gh api repos/$OWNER/$REPO/dependabot/alerts --jq 'group_by(.security_vulnerability.severity) | .[] | {severity: .[0].security_vulnerability.severity, count: length}'
```

---

### Exercise 4.2: Examine a Vulnerability (10 min)

Get details on a specific alert:

```bash
# View alert details
gh api repos/$OWNER/$REPO/dependabot/alerts/1 --jq '{
  package: .security_vulnerability.package.name,
  vulnerable_versions: .security_vulnerability.vulnerable_version_range,
  patched_version: .security_vulnerability.first_patched_version.identifier,
  severity: .security_vulnerability.severity,
  summary: .security_advisory.summary
}'

# See which file declares this dependency
gh api repos/$OWNER/$REPO/dependabot/alerts/1 --jq '.dependency.manifest_path'
```

---

### Exercise 4.3: Enable Automatic Security Updates (5 min)

```bash
# Enable Dependabot security updates
gh api repos/$OWNER/$REPO -X PATCH \
  --input - <<< '{"security_and_analysis":{"dependabot_security_updates":{"status":"enabled"}}}'

# Verify
gh api repos/$OWNER/$REPO --jq '.security_and_analysis.dependabot_security_updates.status'
```

Within a few minutes, Dependabot will create PRs for fixable vulnerabilities:
```bash
gh pr list --author "app/dependabot"
```

---

### Exercise 4.4: Configure Version Updates (10 min)

Set up regular dependency updates:

```bash
# Copy the example configuration
cp .github/dependabot.yml.example .github/dependabot.yml

# View the configuration
cat .github/dependabot.yml
```

Edit the configuration if desired, then commit:

```bash
git add .github/dependabot.yml
git commit -m "Configure Dependabot version updates"
git push
```

---

### Exercise 4.5: Add Dependency Review (5 min)

Add a workflow that blocks PRs introducing vulnerable dependencies:

```bash
# The workflow already exists in the template
cat .github/workflows/dependency-review.yml

# Ensure it is committed
git add .github/workflows/dependency-review.yml
git commit -m "Add dependency review workflow" || echo "Already committed"
git push
```

---

### Phase 4 Checklist

- [ ] Enabled Dependabot alerts
- [ ] Examined vulnerability details
- [ ] Enabled automatic security updates
- [ ] Configured dependabot.yml
- [ ] Added dependency review workflow

---

## Phase 5: Managing Security Across Repositories (35 min)

### What You Will Learn

Organizations have many repositories. Managing security settings individually does not scale. In this phase, you will use Security Overview to see your security posture and create rulesets to enforce standards.

---

### Exercise 5.1: Create a Second Repository (5 min)

```bash
# Create another repository from the template
gh repo create ghas-workshop-secondary --template msftnadavbh/ghas-workshop-template --public

# Enable security features on it
gh api repos/$OWNER/ghas-workshop-secondary/vulnerability-alerts -X PUT
gh api repos/$OWNER/ghas-workshop-secondary -X PATCH \
  --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"}}}'
gh api repos/$OWNER/ghas-workshop-secondary/code-scanning/default-setup -X PATCH -f state=configured
```

---

### Exercise 5.2: Compare Security Posture (15 min)

Query both repositories:

```bash
echo "=== ghas-workshop ==="
echo "Code scanning alerts: $(gh api repos/$OWNER/ghas-workshop/code-scanning/alerts --jq 'length' 2>/dev/null || echo 'N/A')"
echo "Secret alerts: $(gh api repos/$OWNER/ghas-workshop/secret-scanning/alerts --jq 'length' 2>/dev/null || echo 'N/A')"
echo "Dependabot alerts: $(gh api repos/$OWNER/ghas-workshop/dependabot/alerts --jq 'length' 2>/dev/null || echo 'N/A')"

echo ""
echo "=== ghas-workshop-secondary ==="
echo "Code scanning alerts: $(gh api repos/$OWNER/ghas-workshop-secondary/code-scanning/alerts --jq 'length' 2>/dev/null || echo 'N/A')"
echo "Secret alerts: $(gh api repos/$OWNER/ghas-workshop-secondary/secret-scanning/alerts --jq 'length' 2>/dev/null || echo 'N/A')"
echo "Dependabot alerts: $(gh api repos/$OWNER/ghas-workshop-secondary/dependabot/alerts --jq 'length' 2>/dev/null || echo 'N/A')"
```

View Security Overview (if using an organization):
```bash
gh browse -- /security
```

---

### Exercise 5.3: Create a Repository Ruleset (10 min)

Rulesets enforce policies automatically.

**Via browser:**
```bash
gh browse -- /settings/rules
```

1. Click "New ruleset" > "New branch ruleset"
2. **Ruleset name**: Require Security Checks
3. **Enforcement status**: Active
4. **Target branches**: Add target > Include default branch
5. **Rules**: Check "Require status checks to pass"
6. Add "CodeQL" as a required check
7. Click "Create"

---

### Exercise 5.4: Configure Delegated Bypass (5 min)

For push protection, you can require approval before developers bypass:

```bash
gh browse -- /settings/security_analysis
```

1. Find "Push protection"
2. Click "Who can bypass push protection for secret scanning"
3. Select appropriate roles

---

### Phase 5 Checklist

- [ ] Created a second repository
- [ ] Compared security metrics across repositories
- [ ] Created a repository ruleset
- [ ] Reviewed delegated bypass options

---

## Phase 6: Advanced Automation and Custom Detection (40 min)

### What You Will Learn

The GitHub API provides programmatic access to all security data. CodeQL can be extended with custom queries. In this phase, you will build automation and write a custom query.

---

### Exercise 6.1: Build a Security Report Script (10 min)

Create a reusable security report:

```bash
cat > security-report.sh << 'SCRIPT'
#!/bin/bash
REPO="${1:-ghas-workshop}"
OWNER=$(gh api user --jq '.login')

echo "# Security Report: $OWNER/$REPO"
echo "Generated: $(date)"
echo ""

echo "## Code Scanning"
echo "Critical: $(gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '[.[] | select(.rule.security_severity_level=="critical") | select(.state=="open")] | length' 2>/dev/null || echo '0')"
echo "High: $(gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '[.[] | select(.rule.security_severity_level=="high") | select(.state=="open")] | length' 2>/dev/null || echo '0')"
echo ""

echo "## Secret Scanning"
echo "Open: $(gh api repos/$OWNER/$REPO/secret-scanning/alerts --jq '[.[] | select(.state=="open")] | length' 2>/dev/null || echo '0')"
echo ""

echo "## Dependabot"
echo "Critical: $(gh api repos/$OWNER/$REPO/dependabot/alerts --jq '[.[] | select(.security_vulnerability.severity=="critical") | select(.state=="open")] | length' 2>/dev/null || echo '0')"
echo "High: $(gh api repos/$OWNER/$REPO/dependabot/alerts --jq '[.[] | select(.security_vulnerability.severity=="high") | select(.state=="open")] | length' 2>/dev/null || echo '0')"
SCRIPT

chmod +x security-report.sh
./security-report.sh ghas-workshop
```

---

### Exercise 6.2: Write a Custom CodeQL Query (15 min)

Create a query that detects logging of sensitive variables.

First, examine the vulnerable code:
```bash
cat java-backend/src/main/java/com/workshop/ghas/Logger.java
```

Notice how methods like `logUserLogin` log passwords and tokens.

Create the query:
```bash
mkdir -p queries
cat > queries/sensitive-logging.ql << 'QUERY'
/**
 * @name Sensitive data written to log
 * @description Detects logging of variables with sensitive names
 * @kind problem
 * @problem.severity warning
 * @precision medium
 * @id workshop/sensitive-logging
 * @tags security
 */

import java

from MethodAccess logCall, VarAccess varAccess
where
  logCall.getMethod().hasName(["println", "print", "info", "debug", "warn", "error", "log"]) and
  varAccess = logCall.getAnArgument().(VarAccess) and
  varAccess.getVariable().getName().regexpMatch("(?i).*(password|secret|token|key|credential|apikey).*")
select logCall, "Potentially sensitive variable '" + varAccess.getVariable().getName() + "' is logged here"
QUERY

cat queries/sensitive-logging.ql
```

Compare with the solution:
```bash
cat queries/sensitive-logging.ql.solution
```

---

### Exercise 6.3: Plan a Security Campaign (10 min)

Create a plan for remediating alerts:

```bash
cat > security-campaign.md << 'PLAN'
# Security Campaign: Critical Vulnerability Remediation

## Objective
Remediate all critical and high severity alerts within 14 days.

## Scope
- ghas-workshop
- ghas-workshop-secondary

## Timeline
- Days 1-3: Triage all alerts, assign owners
- Days 4-10: Remediation work
- Days 11-14: Review and verification

## Success Criteria
- Zero open critical alerts
- High alerts reduced by 80%

## Tracking
Daily report: ./security-report.sh
PLAN

cat security-campaign.md
```

---

### Exercise 6.4: Set Up Automated Reporting (5 min)

Enable the weekly security report workflow:

```bash
cp .github/workflows/security-report.yml.example .github/workflows/security-report.yml
git add .github/workflows/security-report.yml
git commit -m "Enable weekly security report"
git push
```

Trigger it manually to test:
```bash
gh workflow run security-report.yml
gh run watch
```

---

### Phase 6 Checklist

- [ ] Created security report script
- [ ] Wrote (or reviewed) custom CodeQL query
- [ ] Created security campaign plan
- [ ] Enabled automated reporting

---

## Workshop Complete

You have successfully completed the GitHub Advanced Security workshop.

### Summary of Accomplishments

1. **Phase 1**: Generated an SBOM and established a security policy
2. **Phase 2**: Enabled code scanning and remediated a SQL injection vulnerability
3. **Phase 3**: Tested push protection and implemented a custom secret pattern
4. **Phase 4**: Configured Dependabot for automated dependency management
5. **Phase 5**: Implemented rulesets for security governance at scale
6. **Phase 6**: Built automation with the GitHub API and custom CodeQL queries

### Recommended Next Steps

- Enable Dependabot on your production repositories
- Schedule time to triage and remediate existing security alerts
- Explore the GitHub Security certification program
- Share these practices with your team

### Additional Resources

- [GitHub Security Documentation](https://docs.github.com/en/code-security)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [GitHub Skills](https://skills.github.com/)

---

## Quick Reference

### Common Commands

```bash
# Code scanning
gh api repos/$OWNER/$REPO/code-scanning/alerts

# Secret scanning
gh api repos/$OWNER/$REPO/secret-scanning/alerts

# Dependabot
gh api repos/$OWNER/$REPO/dependabot/alerts

# Enable default code scanning
gh api repos/$OWNER/$REPO/code-scanning/default-setup -X PATCH -f state=configured

# Enable secret scanning (use JSON input for nested objects)
gh api repos/$OWNER/$REPO -X PATCH \
  --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"}}}'

# View in browser
gh browse -- /security
```

### Troubleshooting

| Issue | Solution |
|-------|----------|
| "Code scanning not available" | Repository must be public |
| No alerts appearing | Workflow still running; check `gh run list` |
| Push not blocked | Use CLI instead of web editor |
| Dependabot PRs missing | Wait 5-10 minutes; check Settings |
