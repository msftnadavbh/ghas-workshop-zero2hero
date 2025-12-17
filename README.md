# GitHub Advanced Security Workshop

### Zero to Hero

Learn GitHub Advanced Security hands-on. Work with real vulnerable code, trigger security alerts, and fix them yourself.

*Created by **Nadav Ben Haim***

---

## What You'll Learn

- **Software supply chain**: Generate dependency inventories (SBOM)
- **Code scanning**: Find vulnerabilities with static analysis
- **Secret scanning**: Block credentials before they leak
- **Dependabot**: Automate dependency updates
- **Security at scale**: Manage multiple repositories with rulesets
- **Custom automation**: Use the API and write CodeQL queries

---

## Prerequisites

You need: **Git**, **GitHub CLI**, and a **GitHub account**.

```bash
git --version && gh --version && gh auth status
```

See [Prerequisites Guide](docs/PREREQUISITES.md) for installation instructions.

---

## Quick Start

```bash
gh repo create ghas-workshop --template msftnadavbh/ghas-workshop-zero2hero --public --clone
cd ghas-workshop
```

> Repository must be **public** for free access to security features.

---

## Workshop Phases

| Phase | Topic | Time |
|-------|-------|------|
| 1 | Dependencies & SBOM | 30 min |
| 2 | Code Scanning | 35 min |
| 3 | Secret Scanning | 30 min |
| 4 | Dependabot | 35 min |
| 5 | Security at Scale | 35 min |
| 6 | API & Custom CodeQL | 40 min |

**Total: ~3.5 hours**

---

## Phase 1: Dependencies & SBOM

Generate a Software Bill of Materials.

```bash
# Enable dependency graph
gh api repos/{owner}/{repo}/vulnerability-alerts -X PUT

# Export SBOM
gh api repos/{owner}/{repo}/dependency-graph/sbom > sbom.json
cat sbom.json | jq '.sbom.packages | length'

# Add security policy
cp SECURITY.md.template SECURITY.md
git add SECURITY.md && git commit -m "Add security policy" && git push
```

**Verify:**
```bash
cat sbom.json | jq '.sbom.packages | length'
test -f SECURITY.md && echo "SECURITY.md: OK"
```

---

## Phase 2: Code Scanning

Enable static analysis to find vulnerabilities.

```bash
# Enable code scanning
gh api repos/{owner}/{repo}/code-scanning/default-setup -X PATCH -f state=configured

# Wait for scan, then view alerts
gh api repos/{owner}/{repo}/code-scanning/alerts --jq '.[] | {rule: .rule.id, severity: .rule.security_severity_level, file: .most_recent_instance.location.path}'
```

**Verify:**
```bash
gh api repos/{owner}/{repo}/code-scanning/default-setup --jq '.state'
gh api repos/{owner}/{repo}/code-scanning/alerts --jq 'length'
```

---

## Phase 3: Secret Scanning

Block secrets before they're committed.

```bash
# Enable secret scanning + push protection
gh api repos/{owner}/{repo} -X PATCH \
  --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"},"secret_scanning_push_protection":{"status":"enabled"}}}'

# Test push protection
echo "GITHUB_TOKEN=ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" > test-secret.txt
git add test-secret.txt && git commit -m "Test" && git push
# Push should be blocked

# Clean up
rm test-secret.txt && git reset HEAD~1
```

**Verify:**
```bash
gh api repos/{owner}/{repo} --jq '.security_and_analysis.secret_scanning.status'
gh api repos/{owner}/{repo} --jq '.security_and_analysis.secret_scanning_push_protection.status'
```

---

## Phase 4: Dependabot

Automate dependency updates.

```bash
# Enable Dependabot
gh api repos/{owner}/{repo}/vulnerability-alerts -X PUT
gh api repos/{owner}/{repo} -X PATCH \
  --input - <<< '{"security_and_analysis":{"dependabot_security_updates":{"status":"enabled"}}}'

# Configure version updates
cp .github/dependabot.yml.example .github/dependabot.yml
git add .github/dependabot.yml && git commit -m "Configure Dependabot" && git push

# Check for PRs
gh pr list --author "app/dependabot"
```

**Verify:**
```bash
gh api repos/{owner}/{repo} --jq '.security_and_analysis.dependabot_security_updates.status'
gh api repos/{owner}/{repo}/dependabot/alerts --jq 'length'
```

---

## Phase 5: Security at Scale

Manage security across multiple repositories.

```bash
# Create second repo
gh repo create ghas-workshop-secondary --template msftnadavbh/ghas-workshop-zero2hero --public

# Compare alerts
gh api repos/{owner}/ghas-workshop/code-scanning/alerts --jq 'length'
gh api repos/{owner}/ghas-workshop-secondary/code-scanning/alerts --jq 'length'
```

Create a **repository ruleset** via Settings > Rules > Rulesets to require code scanning.

**Verify:**
```bash
gh api repos/{owner}/{repo}/rulesets --jq 'length'
```

---

## Phase 6: API & Custom CodeQL

Build automation and custom detection.

```bash
# Run security report
./security-report.sh ghas-workshop

# Review custom CodeQL query
cat queries/sensitive-logging.ql.solution

# Enable automated reporting
cp .github/workflows/security-report.yml.example .github/workflows/security-report.yml
git add .github/workflows/security-report.yml && git commit -m "Add security report" && git push
```

**Verify:**
```bash
test -f .github/workflows/security-report.yml && echo "Workflow: OK"
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Code scanning unavailable | Repository must be public |
| No alerts | Scan still running - check `gh run list` |
| Push not blocked | Use CLI, not web editor |
| No Dependabot PRs | Wait 5-10 min |

---

## Resources

- [GitHub Security Docs](https://docs.github.com/en/code-security)
- [CodeQL Docs](https://codeql.github.com/docs/)
- [GitHub Skills](https://skills.github.com/)

---

**Disclaimer**: Contains intentionally vulnerable code for educational purposes only.
