# GitHub Advanced Security Workshop

### Zero to Hero

This hands-on workshop takes a practical approach to learning GitHub Advanced Security. Rather than simply walking through configuration screens, you will work with real vulnerable code, trigger actual security alerts, and implement fixes yourself.

By the end of the workshop, you will understand not just *how* to use GHAS, but *why* each feature matters and when to apply it.

No prior GHAS experience is required. Each phase builds on the previous one, starting with the basics.

*Created by **Nadav Ben Haim***

---

## What You Will Learn

After completing this workshop, you will be able to:

- **Map your software supply chain**: Generate a complete inventory of every dependency in your application
- **Detect vulnerabilities with static analysis**: Enable automated code scanning to identify security issues before they reach production
- **Prevent credential exposure**: Experience push protection blocking a secret in real-time
- **Automate dependency management**: Configure Dependabot to keep your dependencies current and secure
- **Manage security at scale**: Use Security Overview and repository rulesets to enforce standards across multiple repositories
- **Extend with custom automation**: Query the GitHub API and write custom CodeQL detection rules

---

## Before You Start

Review the **[Prerequisites Guide](docs/PREREQUISITES.md)** to ensure your environment is ready. Setup typically takes 10-15 minutes.

**Summary:** You will need Git, GitHub CLI, and a GitHub account.

```bash
# Verify your environment
git --version && gh --version && gh auth status
```

---

## Getting Started

### Step 1: Create Your Workshop Repository

Click the green **"Use this template"** button at the top of this repository, or run:

```bash
gh repo create ghas-workshop --template msftnadavbh/ghas-workshop-zero2hero --public --clone
cd ghas-workshop
```

**Important**: Your repository must be **public** to access all security features for free.

### Step 2: Verify Your Repository

```bash
gh repo view --web
```

This opens your new repository in the browser. You should see the same folder structure as this template.

---

## Workshop Structure

This workshop is divided into six phases, each building on the previous one.

| Phase | Topic | Duration | Difficulty |
|-------|-------|----------|------------|
| 1 | Discovering What is in Your Code | 30 min | Beginner |
| 2 | Finding Vulnerabilities in Your Code | 35 min | Beginner |
| 3 | Preventing Secrets from Leaking | 30 min | Beginner |
| 4 | Automating Dependency Updates | 35 min | Intermediate |
| 5 | Managing Security Across Repositories | 35 min | Intermediate |
| 6 | Advanced Automation and Custom Detection | 40 min | Advanced |

**Total Time**: Approximately 3.5 hours

---

## Phase 1: Discovering What is in Your Code

**Goal**: Understand your software supply chain and what components make up your application.

### What is a Software Bill of Materials?

A Software Bill of Materials (SBOM) is a comprehensive inventory of all components in your software. It documents every library, framework, and dependency your code uses, enabling you to track what requires updating when vulnerabilities are discovered.

### Exercises

**1.1 Explore your dependencies:**
```bash
# See direct Node.js dependencies
cat node-frontend/package.json | grep -A 20 '"dependencies"'

# See Python dependencies
cat python-api/requirements.txt

# See Java dependencies
cat java-backend/pom.xml | grep -A 2 '<dependency>' | head -30
```

**1.2 Generate your SBOM:**
```bash
# Enable the dependency graph
gh api repos/{owner}/{repo}/vulnerability-alerts -X PUT

# Export SBOM
gh api repos/{owner}/{repo}/dependency-graph/sbom > sbom.json

# Count total components
cat sbom.json | jq '.sbom.packages | length'
```

**1.3 Create your security policy:**
```bash
cp SECURITY.md.template SECURITY.md
# Edit SECURITY.md with your contact information
git add SECURITY.md
git commit -m "Add security policy"
git push
```

**Verify Phase 1:**
```bash
# All checks should pass
echo "SBOM packages: $(cat sbom.json | jq '.sbom.packages | length')"
echo "SECURITY.md exists: $(test -f SECURITY.md && echo 'Yes' || echo 'No')"
gh api repos/{owner}/{repo} --jq '"Dependency graph: " + (if .has_vulnerability_alerts_enabled then "Enabled" else "Disabled" end)'
```

See the [Participant Guide](docs/PARTICIPANT_GUIDE.md) for detailed instructions.

---

## Phase 2: Finding Vulnerabilities in Your Code

**Goal**: Enable code scanning and understand how static analysis finds security issues.

### What is Static Analysis?

Static analysis examines your source code without running it, looking for patterns that could lead to security vulnerabilities. It can find issues like SQL injection, cross-site scripting (XSS), and insecure data handling that are easy to miss during code review.

### Exercises

**2.1 Enable code scanning:**
```bash
gh api repos/{owner}/{repo}/code-scanning/default-setup -X PATCH -f state=configured
```

**2.2 View the scan results:**
```bash
# Wait for scan to complete, then list alerts
gh api repos/{owner}/{repo}/code-scanning/alerts --jq '.[] | {rule: .rule.id, severity: .rule.security_severity_level, file: .most_recent_instance.location.path}'
```

**2.3 Fix a vulnerability:**
- Navigate to the Security tab in your repository
- Select a SQL injection alert
- Apply Copilot Autofix or manually fix using parameterized queries

**Verify Phase 2:**
```bash
# Code scanning should be enabled with alerts detected
gh api repos/{owner}/{repo}/code-scanning/default-setup --jq '"Code scanning: " + .state'
gh api repos/{owner}/{repo}/code-scanning/alerts --jq '"Total alerts: " + (length | tostring)'
gh api repos/{owner}/{repo}/code-scanning/alerts --jq '[.[] | select(.state=="fixed")] | "Fixed alerts: " + (length | tostring)'
```

---

## Phase 3: Preventing Secrets from Leaking

**Goal**: Enable secret scanning and experience push protection blocking a commit.

### Why Secret Scanning Matters

Accidentally committed credentials are one of the most common causes of security breaches. Once a secret is in your git history, it is difficult to fully remove and may have already been copied to forks, backups, or search indexes.

### Exercises

**3.1 Enable secret scanning:**
```bash
gh api repos/{owner}/{repo} -X PATCH \
  --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"},"secret_scanning_push_protection":{"status":"enabled"}}}'
```

**3.2 Experience push protection:**
```bash
echo "GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" > test-secret.txt
git add test-secret.txt
git commit -m "Add config"
git push
# Observe the push being blocked
```

**3.3 Clean up:**
```bash
rm test-secret.txt
git reset HEAD~1
```

**Verify Phase 3:**
```bash
# Secret scanning and push protection should be enabled
gh api repos/{owner}/{repo} --jq '"Secret scanning: " + .security_and_analysis.secret_scanning.status'
gh api repos/{owner}/{repo} --jq '"Push protection: " + .security_and_analysis.secret_scanning_push_protection.status'
gh api repos/{owner}/{repo}/secret-scanning/alerts --jq '"Secret alerts: " + (length | tostring)'
```

---

## Phase 4: Automating Dependency Updates

**Goal**: Configure Dependabot to automatically create pull requests for security fixes.

### Exercises

**4.1 Enable Dependabot:**
```bash
gh api repos/{owner}/{repo}/vulnerability-alerts -X PUT
gh api repos/{owner}/{repo} -X PATCH \
  --input - <<< '{"security_and_analysis":{"dependabot_security_updates":{"status":"enabled"}}}'
```

**4.2 Configure version updates:**
```bash
cp .github/dependabot.yml.example .github/dependabot.yml
git add .github/dependabot.yml
git commit -m "Configure Dependabot"
git push
```

**4.3 Review Dependabot PRs:**
```bash
gh pr list --author "app/dependabot"
```

**Verify Phase 4:**
```bash
# Dependabot should be enabled with alerts detected
gh api repos/{owner}/{repo} --jq '"Dependabot security updates: " + .security_and_analysis.dependabot_security_updates.status'
gh api repos/{owner}/{repo}/dependabot/alerts --jq '"Dependabot alerts: " + (length | tostring)'
test -f .github/dependabot.yml && echo "dependabot.yml: Configured" || echo "dependabot.yml: Missing"
```

---

## Phase 5: Managing Security Across Repositories

**Goal**: Use Security Overview and rulesets to manage security at scale.

### Exercises

**5.1 Create a second repository:**
```bash
gh repo create ghas-workshop-secondary --template msftnadavbh/ghas-workshop-zero2hero --public
```

**5.2 Compare security posture:**
```bash
# Get alert counts for both repositories
gh api repos/{owner}/ghas-workshop/code-scanning/alerts --jq 'length'
gh api repos/{owner}/ghas-workshop-secondary/code-scanning/alerts --jq 'length'
```

**5.3 Create a repository ruleset** (via UI):
- Navigate to Settings > Rules > Rulesets
- Create a new branch ruleset requiring code scanning to pass

**Verify Phase 5:**
```bash
# Both repositories should have security features enabled
for repo in ghas-workshop ghas-workshop-secondary; do
  echo "=== $repo ==="
  gh api repos/{owner}/$repo/code-scanning/alerts --jq '"  Code scanning alerts: " + (length | tostring)' 2>/dev/null || echo "  Code scanning: Not configured"
done
gh api repos/{owner}/{repo}/rulesets --jq '"Rulesets configured: " + (length | tostring)'
```

---

## Phase 6: Advanced Automation and Custom Detection

**Goal**: Use the GitHub API and write custom CodeQL queries.

### Exercises

**6.1 Build a security report script:**
```bash
# Create a script that queries all security alerts
./security-report.sh ghas-workshop
```

**6.2 Write a custom CodeQL query:**
See `queries/sensitive-logging.ql.solution` for an example query that detects logging of sensitive variables.

**6.3 Set up automated reporting:**
```bash
cp .github/workflows/security-report.yml.example .github/workflows/security-report.yml
git add .github/workflows/security-report.yml
git commit -m "Add weekly security report"
git push
```

**Verify Phase 6:**
```bash
# Security report script and workflow should be in place
test -x security-report.sh && echo "security-report.sh: Ready" || echo "security-report.sh: Missing or not executable"
test -f .github/workflows/security-report.yml && echo "Automated reporting workflow: Configured" || echo "Automated reporting workflow: Missing"
test -f queries/sensitive-logging.ql.solution && echo "Custom CodeQL query: Available" || echo "Custom CodeQL query: Missing"
```

---

## Repository Contents

```
ghas-workshop-zero2hero/
├── python-api/           # Flask application with SQL injection, path traversal
├── node-frontend/        # Express application with XSS, command injection
├── java-backend/         # Java application with SSRF, XXE vulnerabilities
├── .github/
│   ├── workflows/        # GitHub Actions workflow templates
│   ├── dependabot.yml.example
│   └── CODEOWNERS
├── docs/
│   ├── FACILITATOR_GUIDE.md
│   └── PARTICIPANT_GUIDE.md
├── queries/              # Custom CodeQL query examples
├── solutions/            # Reference solutions for exercises
├── SECURITY.md.template
└── README.md
```

---

## Troubleshooting

### "Code scanning not available"
Your repository is likely private. Security features are free for public repositories. Change visibility in Settings > General.

### "No alerts appearing"
The code scanning workflow may still be running. Check the Actions tab:
```bash
gh run list
```

### "Push not being blocked"
Push protection may not work in the web editor for all patterns. Try using the command line.

### "Dependabot PRs not appearing"
Dependabot PRs can take 5-10 minutes to generate. Check Settings > Code security and analysis.

---

## Additional Resources

- [GitHub Security Documentation](https://docs.github.com/en/code-security)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [GitHub Skills - Security Courses](https://skills.github.com/)

---

## Feedback

Questions, suggestions, or issues? Please [open an issue](../../issues) in this repository.

---

**Disclaimer**: This repository contains intentionally vulnerable code for educational purposes only. Do not deploy this code to production environments.
