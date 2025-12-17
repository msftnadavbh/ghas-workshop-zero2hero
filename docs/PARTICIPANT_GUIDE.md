# Participant Guide

*Created by **Nadav Ben Haim***

Complete exercises for each phase. Work in order - later phases build on earlier ones.

---

## Setup

```bash
# Create your repo
gh repo create ghas-workshop --template msftnadavbh/ghas-workshop-zero2hero --public --clone
cd ghas-workshop

# Set variables
export OWNER=$(gh api user --jq '.login')
export REPO="ghas-workshop"
echo "Working with: $OWNER/$REPO"
```

---

## Phase 1: Dependencies & SBOM (30 min)

### Exercises

**1.1 View dependencies:**
```bash
cat node-frontend/package.json | grep -A 20 '"dependencies"'
cat python-api/requirements.txt
cat java-backend/pom.xml | grep -A 2 '<dependency>' | head -30
```

**1.2 Enable dependency graph:**
```bash
gh api repos/$OWNER/$REPO/vulnerability-alerts -X PUT
gh browse -- /network/dependencies
```

**1.3 Generate SBOM:**
```bash
gh api repos/$OWNER/$REPO/dependency-graph/sbom > sbom.json
cat sbom.json | jq '.sbom.packages | length'
```

**1.4 Add security policy:**
```bash
cp SECURITY.md.template SECURITY.md
git add SECURITY.md && git commit -m "Add security policy" && git push
```

**Checklist:**
- [ ] Viewed dependencies
- [ ] Generated SBOM
- [ ] Committed SECURITY.md

---

## Phase 2: Code Scanning (35 min)

### Exercises

**2.1 Enable code scanning:**
```bash
gh api repos/$OWNER/$REPO/code-scanning/default-setup -X PATCH -f state=configured
gh run watch  # Wait for scan
```

**2.2 View vulnerable code:**
```bash
grep -n "SELECT.*+" python-api/app.py      # SQL injection
grep -n "query}" node-frontend/server.js   # XSS
```

**2.3 View alerts:**
```bash
gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '.[] | {rule: .rule.id, severity: .rule.security_severity_level, file: .most_recent_instance.location.path}'
```

**2.4 Fix a vulnerability:**
- Open alert in browser: `gh browse -- /security/code-scanning`
- Use Copilot Autofix or fix manually with parameterized queries

**Checklist:**
- [ ] Enabled code scanning
- [ ] Viewed alerts
- [ ] Fixed at least one vulnerability

---

## Phase 3: Secret Scanning (30 min)

### Exercises

**3.1 Enable secret scanning:**
```bash
gh api repos/$OWNER/$REPO -X PATCH \
  --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"},"secret_scanning_push_protection":{"status":"enabled"}}}'
```

**3.2 View existing alerts:**
```bash
gh api repos/$OWNER/$REPO/secret-scanning/alerts --jq '.[] | {type: .secret_type, state: .state}'
```

**3.3 Test push protection:**
```bash
echo "GITHUB_TOKEN=ghp_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef12" > test-secret.txt
git add test-secret.txt && git commit -m "Test" && git push
# Should be blocked

rm test-secret.txt && git reset HEAD~1
```

**3.4 Create custom pattern** (via browser):
- Go to Settings > Code security > Custom patterns
- Pattern: `WORKSHOP-[A-Z0-9]{16}`

**Checklist:**
- [ ] Enabled secret scanning
- [ ] Experienced push protection block
- [ ] Created custom pattern

---

## Phase 4: Dependabot (35 min)

### Exercises

**4.1 Enable Dependabot:**
```bash
gh api repos/$OWNER/$REPO/vulnerability-alerts -X PUT
gh api repos/$OWNER/$REPO -X PATCH \
  --input - <<< '{"security_and_analysis":{"dependabot_security_updates":{"status":"enabled"}}}'
```

**4.2 View alerts:**
```bash
gh api repos/$OWNER/$REPO/dependabot/alerts --jq '.[] | {package: .security_vulnerability.package.name, severity: .security_vulnerability.severity}'
```

**4.3 Configure version updates:**
```bash
cp .github/dependabot.yml.example .github/dependabot.yml
git add .github/dependabot.yml && git commit -m "Configure Dependabot" && git push
```

**4.4 Check PRs:**
```bash
gh pr list --author "app/dependabot"
```

**Checklist:**
- [ ] Enabled Dependabot alerts
- [ ] Enabled security updates
- [ ] Configured dependabot.yml

---

## Phase 5: Security at Scale (35 min)

### Exercises

**5.1 Create second repo:**
```bash
gh repo create ghas-workshop-secondary --template msftnadavbh/ghas-workshop-zero2hero --public
gh api repos/$OWNER/ghas-workshop-secondary/code-scanning/default-setup -X PATCH -f state=configured
```

**5.2 Compare repos:**
```bash
echo "ghas-workshop: $(gh api repos/$OWNER/ghas-workshop/code-scanning/alerts --jq 'length') alerts"
echo "ghas-workshop-secondary: $(gh api repos/$OWNER/ghas-workshop-secondary/code-scanning/alerts --jq 'length') alerts"
```

**5.3 Create ruleset** (via browser):
- Settings > Rules > Rulesets > New branch ruleset
- Require status checks: CodeQL

**Checklist:**
- [ ] Created second repo
- [ ] Compared security posture
- [ ] Created ruleset

---

## Phase 6: API & Custom CodeQL (40 min)

### Exercises

**6.1 Security report:**
```bash
chmod +x security-report.sh
./security-report.sh ghas-workshop
```

**6.2 Review custom CodeQL:**
```bash
cat queries/sensitive-logging.ql.solution
```

**6.3 Enable automated reporting:**
```bash
cp .github/workflows/security-report.yml.example .github/workflows/security-report.yml
git add .github/workflows/security-report.yml && git commit -m "Add security report" && git push
```

**Checklist:**
- [ ] Ran security report
- [ ] Reviewed CodeQL query
- [ ] Enabled automated reporting

---

## Quick Reference

```bash
# Alerts
gh api repos/$OWNER/$REPO/code-scanning/alerts
gh api repos/$OWNER/$REPO/secret-scanning/alerts
gh api repos/$OWNER/$REPO/dependabot/alerts

# Enable features
gh api repos/$OWNER/$REPO/code-scanning/default-setup -X PATCH -f state=configured
gh api repos/$OWNER/$REPO -X PATCH --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"}}}'

# Browser
gh browse -- /security
```

---

## Done!

You've completed the workshop. Next steps:
- Enable these features on your real repositories
- Triage existing alerts
- Share with your team
