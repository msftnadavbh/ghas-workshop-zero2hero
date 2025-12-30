# Participant Guide

*Created by **Nadav Ben Haim***

Welcome to the GitHub Advanced Security Workshop! This guide will walk you through each phase step-by-step. No prior GHAS experience is required-just follow along.

---

## Setup

Before diving in, let's create your workshop environment.

**Create your repository from the template:**

```bash
gh repo create ghas-workshop --template msftnadavbh/ghas-workshop-zero2hero --public --clone
cd ghas-workshop
```

This command uses the GitHub CLI (`gh`) to create a new public repository in your account, using this workshop as a template. The `--clone` flag automatically downloads it to your machine.

**Set up environment variables:**

We'll use these variables throughout the workshop so you don't have to type your username repeatedly.

```bash
export OWNER=$(gh api user --jq '.login')
export REPO="ghas-workshop"
echo "Working with: $OWNER/$REPO"
```

You should see your GitHub username printed. If not, run `gh auth login` to authenticate.

---

## Phase 1: Dependencies & SBOM (30 min)

> **Your Mission:** Supply chain attacks are on the rise-attackers target the libraries you depend on, not just your code. Before you can secure your supply chain, you need to know what's in it. Your first task is to generate a complete inventory of every dependency in this project.

### What You'll Learn
- How to view dependencies across different languages (Node.js, Python, Java)
- What a Software Bill of Materials (SBOM) is and why it matters
- How to enable GitHub's dependency tracking

### Exercises

**1.1 Explore the vulnerable dependencies**

This repository intentionally uses outdated packages with known vulnerabilities. Let's see what we're working with.

```bash
cat node-frontend/package.json | grep -A 20 '"dependencies"'
```

This displays the Node.js dependencies. Notice versions like `lodash: 4.17.15`-these are intentionally old.

```bash
cat python-api/requirements.txt
```

The Python dependencies. Look for packages like `Flask==1.0.0` and `PyYAML==5.1`.

```bash
cat java-backend/pom.xml | grep -A 2 '<dependency>' | head -30
```

The Java dependencies in Maven format. You'll see `log4j-core` version `2.14.0`-the infamous Log4Shell vulnerability.

**1.2 Enable the Dependency Graph**

The Dependency Graph is GitHub's feature that automatically detects and maps all dependencies in your repository. Without it, GitHub can't alert you to vulnerable packages.

```bash
gh api repos/$OWNER/$REPO/vulnerability-alerts -X PUT
```

This API call enables vulnerability alerts, which automatically turns on the Dependency Graph.

**Open it in your browser:**

```bash
echo "https://github.com/$OWNER/$REPO/network/dependencies"
```

Click the URL or copy it to your browser. You should see a visual map of all packages used across the three applications.

**1.3 Generate a Software Bill of Materials (SBOM)**

An SBOM is a formal, machine-readable inventory of all components in your software. It's increasingly required for compliance and security audits.

```bash
gh api repos/$OWNER/$REPO/dependency-graph/sbom > sbom.json
```

This exports your SBOM in SPDX format (an industry standard).

**Check how many packages were detected:**

```bash
cat sbom.json | jq '.sbom.packages | length'
```

You should see a number representing all direct and transitive dependencies.

**1.4 Add a Security Policy**

A `SECURITY.md` file tells users how to report vulnerabilities responsibly. It's a best practice for any open-source project.

```bash
cp SECURITY.md.template SECURITY.md
git add SECURITY.md && git commit -m "Add security policy" && git push
```

**✅ Phase 1 Complete!** You now have visibility into your entire software supply chain.

---

## Phase 2: Code Scanning (35 min)

> **Your Mission:** Vulnerabilities hide in plain sight-SQL Injection, Cross-Site Scripting, and Remote Code Execution are lurking in this codebase. Manual code review can't catch everything. Deploy CodeQL, GitHub's semantic code analysis engine, to automatically hunt down these flaws.

### What You'll Learn
- How CodeQL analyzes code for security vulnerabilities
- How to read and understand Code Scanning alerts
- How to fix a real vulnerability

### Exercises

**2.1 Enable Code Scanning with CodeQL**

CodeQL is a powerful static analysis engine that treats code like data. It can find complex vulnerabilities that simple pattern matching would miss.

```bash
gh api repos/$OWNER/$REPO/code-scanning/default-setup -X PATCH -f state=configured
```

This enables "Default Setup"-GitHub will automatically configure CodeQL for all supported languages in your repository (Python, JavaScript, Java).

**Wait for the scan to complete:**

```bash
gh run watch
```

This shows the progress of the GitHub Actions workflow. The first scan typically takes 3-5 minutes.

**2.2 Explore the vulnerable code (while you wait)**

Let's look at what CodeQL is hunting for.

**SQL Injection in Python:**
```bash
grep -n "SELECT.*+" python-api/app.py
```

Look at the results. You'll see code like `"SELECT * FROM users WHERE id = " + user_id`. This is vulnerable because user input is directly concatenated into the SQL query-an attacker could input `1 OR 1=1` to dump the entire database.

**Cross-Site Scripting (XSS) in JavaScript:**
```bash
grep -n "query}" node-frontend/server.js
```

You'll find template strings like `${query}` being inserted directly into HTML. An attacker could input `<script>alert('hacked')</script>` and execute arbitrary JavaScript in users' browsers.

**2.3 View the Code Scanning alerts**

Once the scan completes, let's see what CodeQL found.

```bash
gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '.[] | {rule: .rule.id, severity: .rule.security_severity_level, file: .most_recent_instance.location.path}'
```

This lists all detected vulnerabilities with their severity and location.

**Open the Security tab in your browser for a detailed view:**

```bash
gh browse -- /security/code-scanning
```

Click on any alert to see:
- A description of the vulnerability
- The exact line of code
- A data flow diagram showing how untrusted input reaches a dangerous function
- Remediation guidance

**2.4 Fix a vulnerability**

Pick one alert and fix it. For SQL Injection, the fix is to use **parameterized queries** instead of string concatenation.

**Example fix for Python:**
```python
# Vulnerable:
query = "SELECT * FROM users WHERE id = " + user_id

# Fixed:
cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))
```

After fixing, commit and push your change. CodeQL will automatically re-scan and close the alert if the fix is correct.

> 💡 **Tip:** GitHub Copilot can suggest fixes! Click "Generate fix" on any alert in the browser.

**✅ Phase 2 Complete!** You've deployed automated vulnerability detection and fixed real security flaws.

---

## Phase 3: Secret Scanning (30 min)

> **Your Mission:** Leaked credentials are a nightmare. API keys, database passwords, and tokens committed to git history can be harvested by attackers within seconds. Your task is to enable Secret Scanning to detect existing leaks and Push Protection to prevent new ones.

### What You'll Learn
- How Secret Scanning detects over 200 types of secrets
- How Push Protection blocks secrets before they enter the repository
- How to create custom secret patterns for internal credentials

### Exercises

**3.1 Enable Secret Scanning and Push Protection**

Secret Scanning continuously monitors your repository for known credential patterns. Push Protection goes further-it blocks `git push` if a secret is detected.

```bash
gh api repos/$OWNER/$REPO -X PATCH \
  --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"},"secret_scanning_push_protection":{"status":"enabled"}}}'
```

This single API call enables both features.

**3.2 Check for existing secrets**

This codebase has some hardcoded credentials. Let's see if Secret Scanning found them.

```bash
gh api repos/$OWNER/$REPO/secret-scanning/alerts --jq '.[] | {type: .secret_type, state: .state, file: .locations[0].details.path}'
```

If any secrets were detected, you'll see them listed with their type (e.g., `github_personal_access_token`).

**View them in the browser:**

```bash
gh browse -- /security/secret-scanning
```

**3.3 Experience Push Protection in action**

Let's intentionally try to commit a secret and see Push Protection block it.

```bash
echo "GITHUB_TOKEN=ghp_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdef12" > test-secret.txt
git add test-secret.txt
git commit -m "Test secret"
git push
```

**You should see an error!** GitHub detected the fake token pattern and refused the push. This is Push Protection working.

**Clean up:**

```bash
rm test-secret.txt
git reset HEAD~1
```

**3.4 Create a custom secret pattern**

Your organization might have internal credential formats that GitHub doesn't recognize by default. You can define custom patterns.

**In your browser:**
1. Go to your repository → Settings → Code security and analysis
2. Scroll to "Custom patterns"
3. Click "New pattern"
4. Name: `Workshop Internal Key`
5. Pattern: `WORKSHOP-[A-Z0-9]{16}`
6. Save

Now any string like `WORKSHOP-ABC123XYZ789DEF0` will be flagged.

**✅ Phase 3 Complete!** You've secured your repository against credential leaks-past and future.

---

## Phase 4: Dependabot (35 min)

> **Your Mission:** Keeping dependencies up to date is tedious but critical. Every week, new CVEs are published for popular packages. Dependabot automates this-it monitors your dependencies and automatically creates pull requests to update vulnerable packages.

### What You'll Learn
- How Dependabot alerts notify you of vulnerable dependencies
- How Dependabot security updates automatically create fix PRs
- How to configure scheduled version updates

### Exercises

**4.1 Enable Dependabot Alerts and Security Updates**

Dependabot Alerts warn you when a dependency has a known vulnerability. Security Updates go further-they automatically open PRs to fix the issue.

```bash
gh api repos/$OWNER/$REPO/vulnerability-alerts -X PUT
gh api repos/$OWNER/$REPO -X PATCH \
  --input - <<< '{"security_and_analysis":{"dependabot_security_updates":{"status":"enabled"}}}'
```

**4.2 View Dependabot Alerts**

With all the intentionally outdated dependencies in this repo, Dependabot should have a lot to say.

```bash
gh api repos/$OWNER/$REPO/dependabot/alerts --jq '.[] | {package: .security_vulnerability.package.name, severity: .security_vulnerability.severity, vulnerable_version: .security_vulnerability.vulnerable_version_range}'
```

This lists every vulnerable package, its severity, and the affected versions.

**Open in browser for full details:**

```bash
gh browse -- /security/dependabot
```

Click on any alert to see:
- CVE details and CVSS score
- Which version fixes the issue
- A link to the security advisory

**4.3 Configure automated version updates**

Beyond security fixes, Dependabot can keep all your dependencies current with scheduled version updates.

```bash
cp .github/dependabot.yml.example .github/dependabot.yml
git add .github/dependabot.yml
git commit -m "Configure Dependabot version updates"
git push
```

**View the configuration:**

```bash
cat .github/dependabot.yml
```

This file tells Dependabot which package ecosystems to monitor (npm, pip, maven) and how often to check for updates.

**4.4 Check for Dependabot PRs**

If security updates are enabled, Dependabot may have already created PRs to fix vulnerabilities.

```bash
gh pr list --author "app/dependabot"
```

If you see PRs, review one! Dependabot PRs include:
- What changed and why
- Compatibility score
- Release notes from the package

> 💡 **Tip:** Give it 5-10 minutes. Dependabot runs on a schedule.

**✅ Phase 4 Complete!** Your dependencies are now automatically monitored and updated.

---

## Phase 5: Security at Scale (35 min)

> **Your Mission:** Everything you've done works for one repository. But what about 100? 1,000? Organizations need to enforce security policies across all repositories consistently. Rulesets let you do exactly that-define rules once, apply everywhere.

### What You'll Learn
- How to manage security across multiple repositories
- How Repository Rulesets enforce security requirements
- How to require code scanning before merging

### Exercises

**5.1 Create a second repository**

To demonstrate security at scale, let's create another repo from the same template.

```bash
gh repo create ghas-workshop-secondary --template msftnadavbh/ghas-workshop-zero2hero --public
```

Enable code scanning on it:

```bash
gh api repos/$OWNER/ghas-workshop-secondary/code-scanning/default-setup -X PATCH -f state=configured
```

**5.2 Compare security posture**

Now you have two repositories with the same vulnerabilities. Let's compare.

```bash
echo "=== ghas-workshop ==="
echo "Code Scanning alerts: $(gh api repos/$OWNER/ghas-workshop/code-scanning/alerts --jq 'length')"
echo "Dependabot alerts: $(gh api repos/$OWNER/ghas-workshop/dependabot/alerts --jq 'length')"

echo ""
echo "=== ghas-workshop-secondary ==="
echo "Code Scanning alerts: $(gh api repos/$OWNER/ghas-workshop-secondary/code-scanning/alerts --jq 'length')"
echo "Dependabot alerts: $(gh api repos/$OWNER/ghas-workshop-secondary/dependabot/alerts --jq 'length')"
```

Imagine doing this for hundreds of repositories-you'd need automation.

**5.3 Create a Branch Ruleset**

Rulesets let you enforce policies like "No merging until CodeQL passes." Let's create one.

**In your browser:**
1. Go to your `ghas-workshop` repository
2. Settings → Rules → Rulesets
3. Click "New ruleset" → "New branch ruleset"
4. Name: `Require Security Checks`
5. Enforcement: Active
6. Target branches: Default branch
7. Check "Require status checks to pass"
8. Add required check: `CodeQL`
9. Save

Now, any PR to `main` must pass CodeQL before merging.

**5.4 Test the ruleset**

Create a branch with a vulnerability and try to merge it:

```bash
git checkout -b test-vulnerability
echo 'eval(input())' >> python-api/app.py
git add . && git commit -m "Add vulnerability"
git push -u origin test-vulnerability
gh pr create --title "Test PR" --body "Testing ruleset"
```

The PR should be blocked from merging until CodeQL passes (and it won't, because you just added a code injection vulnerability).

**Clean up:**

```bash
git checkout main
git branch -D test-vulnerability
gh pr close --delete-branch
```

**✅ Phase 5 Complete!** You now know how to enforce security standards across your organization.

---

## Phase 6: API & Custom CodeQL (40 min)

> **Your Mission:** Out-of-the-box tools catch common vulnerabilities, but every codebase has unique risks. In this final phase, you'll use the GitHub API to build a custom security dashboard and explore writing your own CodeQL query to catch application-specific bugs.

### What You'll Learn
- How to use the GitHub Security API for custom reporting
- How CodeQL queries work
- How to run custom queries in your CI/CD pipeline

### Exercises

**6.1 Run the security report script**

This repository includes a script that uses the GitHub API to generate a security summary.

```bash
chmod +x scripts/security-report.sh
./scripts/security-report.sh ghas-workshop
```

The script queries multiple API endpoints and consolidates the data. Review the output-it shows alerts across Code Scanning, Secret Scanning, and Dependabot.

**Open the script to see how it works:**

```bash
cat scripts/security-report.sh | head -50
```

This is the foundation for building dashboards, Slack notifications, or compliance reports.

**6.2 Explore the custom CodeQL query**

CodeQL lets you write custom queries for vulnerabilities specific to your application.

**View the starter query:**

```bash
cat queries/sensitive-logging.ql
```

This query is designed to detect when sensitive data (passwords, tokens) is written to logs-a common security anti-pattern.

**View the completed solution:**

```bash
cat queries/sensitive-logging.ql.solution
```

The query:
1. Identifies logging methods (`System.out.println`, `logger.info`, etc.)
2. Finds variables with sensitive names (`password`, `token`, `secret`)
3. Alerts when those variables flow into log statements

**6.3 Enable automated security reporting**

Let's add a GitHub Actions workflow that runs the security report on a schedule.

```bash
cp .github/workflows/security-report.yml.example .github/workflows/security-report.yml
git add .github/workflows/security-report.yml
git commit -m "Enable automated security reporting"
git push
```

This workflow runs weekly and can be extended to post results to Slack or create issues.

**✅ Phase 6 Complete!** You've graduated from user to power user-building custom tooling on top of GitHub's security platform.

---

## Quick Reference

**View alerts:**
```bash
gh api repos/$OWNER/$REPO/code-scanning/alerts
gh api repos/$OWNER/$REPO/secret-scanning/alerts
gh api repos/$OWNER/$REPO/dependabot/alerts
```

**Enable features:**
```bash
# Code Scanning
gh api repos/$OWNER/$REPO/code-scanning/default-setup -X PATCH -f state=configured

# Secret Scanning + Push Protection
gh api repos/$OWNER/$REPO -X PATCH --input - <<< '{"security_and_analysis":{"secret_scanning":{"status":"enabled"},"secret_scanning_push_protection":{"status":"enabled"}}}'

# Dependabot
gh api repos/$OWNER/$REPO/vulnerability-alerts -X PUT
```

**Open in browser:**
```bash
gh browse -- /security
```

---

## 🎉 Congratulations!

You've completed the GitHub Advanced Security Workshop. You now know how to:

- ✅ Map your software supply chain with SBOM
- ✅ Find and fix code vulnerabilities with CodeQL
- ✅ Prevent and detect leaked secrets
- ✅ Automate dependency updates with Dependabot
- ✅ Enforce security policies at scale with Rulesets
- ✅ Build custom security tooling with the API

**Next Steps:**
1. Enable GHAS on your real repositories
2. Triage and fix existing alerts
3. Set up Rulesets for your organization
4. Share this workshop with your team

---

*Questions? Issues? Open an issue in this repository or reach out to your facilitator.*
