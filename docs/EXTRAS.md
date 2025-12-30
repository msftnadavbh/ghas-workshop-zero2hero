# GitHub Advanced Security: Features & Licensing

*A practical guide to what's included, what requires licensing, and how to get the most from GHAS*

---

## 📊 Feature Availability

| Feature | Public Repos | Private Repos (Free) | Private Repos (GHAS License) |
|---------|:------------:|:-------------------:|:--------------------:|
| **Dependency Graph** | ✅ | ✅ | ✅ |
| **Dependabot Alerts** | ✅ | ✅ | ✅ |
| **Dependabot Security Updates** | ✅ | ✅ | ✅ |
| **Dependabot Version Updates** | ✅ | ✅ | ✅ |
| **Security Advisories** | ✅ | ✅ | ✅ |
| **Code Scanning (CodeQL)** | ✅ | ❌ | ✅ |
| **Secret Scanning** | ✅ | ❌ | ✅ |
| **Push Protection** | ✅ | ❌ | ✅ |
| **Custom Secret Patterns** | ✅ | ❌ | ✅ |
| **Security Overview** | — | ❌ | ✅ |
| **Security Campaigns** | — | ❌ | ✅ |
| **Copilot Autofix*** | ✅ | ❌ | ✅ |
| **Dependency Review Action** | ✅ | ❌ | ✅ |
| **Security Configurations** | — | ❌ | ✅ |

*\*Requires GitHub Copilot subscription*

---

## Feature Deep Dive

### Code Scanning with CodeQL

CodeQL is a semantic code analysis engine that finds vulnerabilities by treating code as data. Unlike regex-based scanners, it understands data flow and can trace how user input moves through your application.

**What it detects:**
- SQL Injection, XSS, Command Injection
- Path traversal, insecure deserialization
- Authentication bypasses, cryptographic weaknesses
- Language-specific vulnerabilities (40+ CWEs per language)

**Supported languages:** Python, JavaScript/TypeScript, Java, C/C++, C#, Go, Ruby, Swift, Kotlin

**Custom queries:** Write organization-specific rules using the CodeQL query language. See the `queries/` folder for examples.

---

### Secret Scanning & Push Protection

Secret Scanning detects credentials that have been committed to your repository. Push Protection goes further by blocking secrets *before* they're pushed.

**Detection coverage:**
- 200+ secret types from partners (AWS, Azure, GCP, Slack, etc.)
- GitHub tokens (PATs, OAuth, App tokens)
- Generic patterns (private keys, connection strings)
- Custom patterns you define

**How Push Protection works:**
1. Developer runs `git push`
2. GitHub scans the commits for secrets
3. If found, push is blocked with remediation guidance
4. Developer can remove the secret or (if false positive) bypass with justification

**Partner program:** When GitHub detects a partner's secret (e.g., an AWS key), the partner is notified automatically so they can revoke it.

---

### Security Overview

Security Overview provides organization-wide visibility into your security posture across all repositories.

**Key views:**
- **Risk:** Open alerts by severity across all repos
- **Coverage:** Which repos have GHAS features enabled
- **Trends:** Alert counts over time (are you improving?)
- **Enablement:** Quickly enable features across multiple repos

**Use cases:**
- Identify repos without code scanning enabled
- Track remediation progress across teams
- Generate compliance reports
- Prioritize which repos need attention

---

### Security Campaigns

Security Campaigns help coordinate large-scale remediation efforts across your organization.

**How it works:**
1. Create a campaign (e.g., "Q1 Critical Remediation")
2. Add alerts from one or multiple repositories
3. Assign to teams or individuals
4. Set target dates
5. Track progress in a unified dashboard

**Best for:**
- Coordinating fixes across multiple teams
- Time-bound remediation initiatives
- Compliance deadlines

---

### Copilot Autofix

Copilot Autofix uses AI to generate fixes for code scanning alerts. It analyzes the vulnerability, understands the code context, and suggests a patch.

**What it does:**
- Generates context-aware fix suggestions
- Creates PRs with the proposed changes
- Works for most common vulnerability types

**What it doesn't do:**
- Replace human review (always verify fixes)
- Fix every vulnerability type
- Guarantee correctness (test thoroughly)

**Requirements:** GitHub Copilot subscription + GHAS license (for private repos)

---

### Dependency Review Action

The Dependency Review Action runs on pull requests and blocks merges if the PR introduces vulnerable dependencies.

**Configuration options:**
- Block by severity (critical, high, medium, low)
- Block by license type (GPL, AGPL, etc.)
- Allow specific vulnerabilities (with justification)

**Example workflow:**
```yaml
- name: Dependency Review
  uses: actions/dependency-review-action@v4
  with:
    fail-on-severity: moderate
    deny-licenses: GPL-3.0, AGPL-3.0
```

---

## Licensing Model

GHAS is licensed **per active committer**. An active committer is someone who has pushed code to a GHAS-enabled private repository in the last 90 days.

**What counts:**
- Commits to GHAS-enabled private repos
- Bot accounts that commit code

**What doesn't count:**
- Read-only access
- Commits to public repos (GHAS is free)
- Inactive users (no commits in 90 days)

**Pricing:** See [github.com/pricing](https://github.com/pricing) for current rates.

---

## Compliance & Audit

GHAS provides capabilities that support various compliance frameworks:

| Framework | Relevant Capabilities |
|-----------|----------------------|
| **SOC 2** | Automated vulnerability scanning, audit logs, evidence of remediation |
| **ISO 27001** | Vulnerability management, dependency tracking, access controls |
| **PCI DSS** | Code review requirements (6.3.2), vulnerability scanning (11.3) |
| **HIPAA** | Access audit trails, security monitoring |
| **FedRAMP** | Continuous monitoring, automated remediation tracking |

**Useful for audits:**
- SBOM export (SPDX format) for software inventory
- Alert history showing detection and remediation dates
- Security Overview reports for coverage metrics
- API access for custom compliance reporting

---

## Advanced Capabilities

### SARIF Upload

Integrate third-party scanners by uploading results in SARIF format:

```yaml
- name: Upload SARIF
  uses: github/codeql-action/upload-sarif@v3
  with:
    sarif_file: results.sarif
```

All results appear in the same Security tab alongside CodeQL findings.

### Custom CodeQL Query Packs

Create and share organization-specific queries:

```bash
# Initialize a query pack
codeql pack init my-org/security-queries

# Publish to GitHub Packages
codeql pack publish
```

### Webhook Notifications

Subscribe to security events for integration with external systems:

```bash
gh api repos/$OWNER/$REPO/hooks -X POST \
  -f name=web \
  -f config[url]="https://your-system.com/webhook" \
  -f config[content_type]=json \
  -f events[]=code_scanning_alert \
  -f events[]=secret_scanning_alert
```

### API Access

Full API access for automation and custom tooling:

```bash
# List all open code scanning alerts
gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '.[] | select(.state=="open")'

# Organization-wide secret scanning alerts
gh api orgs/$OWNER/secret-scanning/alerts

# Export SBOM
gh api repos/$OWNER/$REPO/dependency-graph/sbom
```

---

## Getting Started

### For Public Repositories
All GHAS features are free. Enable them in Settings → Code security and analysis.

### For Private Repositories
1. Start a [30-day trial](https://github.com/enterprise/trial)
2. Enable on a few repositories to evaluate
3. Review the alerts and remediation experience
4. Expand to more repositories as needed

### Recommended Rollout
1. **Start small:** Enable on 2-3 high-priority repos
2. **Fix critical alerts:** Build confidence with quick wins
3. **Enable rulesets:** Prevent new vulnerabilities from merging
4. **Expand coverage:** Roll out to more repos using Security Configurations
5. **Track progress:** Use Security Overview to measure improvement

---

## Resources

- [GitHub Advanced Security Documentation](https://docs.github.com/en/code-security)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [CodeQL Query Library](https://github.com/github/codeql)
- [GitHub Security Lab](https://securitylab.github.com/)
- [GHAS Certification](https://resources.github.com/learn/certifications/)
- [GitHub Community Discussions](https://github.com/orgs/community/discussions)
