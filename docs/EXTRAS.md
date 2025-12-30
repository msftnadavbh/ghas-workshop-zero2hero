# GitHub Advanced Security: Extras & Licensing Guide

*Understanding what's free, what's paid, and why GHAS is worth the investment*

---

## 📊 Feature Availability Matrix

| Feature | Public Repos | Private Repos (Free) | Private Repos (GHAS) |
|---------|:------------:|:-------------------:|:--------------------:|
| **Dependency Graph** | ✅ Free | ✅ Free | ✅ Included |
| **Dependabot Alerts** | ✅ Free | ✅ Free | ✅ Included |
| **Dependabot Security Updates** | ✅ Free | ✅ Free | ✅ Included |
| **Dependabot Version Updates** | ✅ Free | ✅ Free | ✅ Included |
| **Security Advisories** | ✅ Free | ✅ Free | ✅ Included |
| **Code Scanning (CodeQL)** | ✅ Free | ❌ | ✅ Included |
| **Secret Scanning** | ✅ Free | ❌ | ✅ Included |
| **Push Protection** | ✅ Free | ❌ | ✅ Included |
| **Custom Secret Patterns** | ✅ Free | ❌ | ✅ Included |
| **Security Overview** | ➖ N/A | ❌ | ✅ Included |
| **Security Campaigns** | ➖ N/A | ❌ | ✅ Included |
| **Copilot Autofix** | ✅ Free* | ❌ | ✅ Included* |
| **Dependency Review Action** | ✅ Free | ❌ | ✅ Included |
| **Security Configurations** | ➖ N/A | ❌ | ✅ Included |

*\*Requires GitHub Copilot subscription*

---

## 💰 Why Pay for GHAS?

### The Real Cost of NOT Having GHAS

Before dismissing GHAS as "just another expense," consider what security incidents actually cost:

| Incident Type | Average Cost | GHAS Prevention |
|--------------|-------------|-----------------|
| **Data Breach** | $4.45M (IBM 2023) | Secret Scanning, Push Protection |
| **Ransomware** | $1.85M average ransom | Code Scanning catches injection flaws |
| **Supply Chain Attack** | $4.76M (Ponemon) | Dependency Graph, Dependabot |
| **Compliance Fine (GDPR)** | Up to €20M or 4% revenue | Security Overview, audit trails |
| **Developer Time (manual review)** | ~$150k/year per dev | Autofix, automated scanning |

> 💡 **One prevented breach pays for years of GHAS licensing.**

---

## 🎯 ROI Breakdown by Feature

### 1. Code Scanning with CodeQL

**What it does:** Finds vulnerabilities in your code before they reach production.

**Without GHAS:**
- Manual code review: 2-4 hours per PR
- External SAST tools: $50-200k/year
- Missed vulnerabilities → breaches

**With GHAS:**
- Automatic scanning on every push
- Zero additional tooling cost
- Catches OWASP Top 10 vulnerabilities
- Deep semantic analysis (not just regex)

**ROI:** A single SQL injection vulnerability in production could cost $200k+ in incident response, legal fees, and reputation damage. CodeQL catches these for ~$50/developer/month.

---

### 2. Secret Scanning & Push Protection

**What it does:** Prevents credentials from being committed and detects existing leaks.

**The Reality:**
- 30% of data breaches involve leaked credentials (Verizon DBIR)
- Average time to detect leaked secrets: 327 days
- Bots scrape GitHub for secrets within seconds of commit

**Without GHAS:**
- Secrets live in git history forever
- Manual rotation after discovery
- Hope no one finds them

**With GHAS:**
- **Push Protection blocks secrets at git push** (before they hit GitHub)
- Automatic detection of 200+ secret types
- Partner alerts notify providers (AWS, Azure, etc.) immediately
- Custom patterns for internal credentials

**ROI:** One leaked AWS key can spin up $100k in crypto mining overnight. Push Protection prevents this entirely.

---

### 3. Security Overview & Campaigns

**What it does:** Provides organization-wide visibility and coordinates remediation.

**The Enterprise Problem:**
- 500+ repositories
- 50+ development teams
- "Which repos have security enabled?"
- "Are we actually fixing alerts?"

**Without GHAS:**
- Spreadsheets and manual audits
- No visibility into coverage gaps
- Security team plays whack-a-mole

**With GHAS:**
- **Single dashboard** for all repos
- Coverage metrics (% with scanning enabled)
- Alert trends over time
- **Security Campaigns**: Assign alerts to teams, track progress
- Export data for compliance audits

**ROI:** Security teams spend 60% less time on reporting and can focus on actual security work.

---

### 4. Copilot Autofix

**What it does:** AI generates fixes for security vulnerabilities.

**The Developer Experience Problem:**
- Developers aren't security experts
- "I found a vulnerability... now what?"
- Fixing takes longer than finding

**Without Autofix:**
- Developer researches vulnerability
- Reads documentation
- Writes fix
- Hopes it's correct
- **Time: 30 minutes to 2 hours per alert**

**With Autofix:**
- Click "Generate fix"
- Review AI-suggested patch
- Commit
- **Time: 2-5 minutes per alert**

**ROI:** If you have 100 alerts and save 30 minutes each, that's 50 hours of developer time saved. At $100/hour, that's $5,000 per remediation cycle.

---

## 🏢 GHAS for Different Organization Sizes

### Startups (< 50 developers)
- **Use free features** on public repos
- Enable Dependabot everywhere (free)
- Consider GHAS when you have private repos with customer data

### Mid-Market (50-500 developers)
- **GHAS is essential**
- Security team can't manually review everything
- Compliance requirements (SOC2, HIPAA) often mandate SAST
- ROI is immediate with Security Overview

### Enterprise (500+ developers)
- **GHAS is table stakes**
- Competitors are already using it
- Security Campaigns coordinate thousands of alerts
- Custom CodeQL queries for organization-specific risks
- Integrations with Jira, ServiceNow, Splunk

---

## 📋 Compliance & Audit Benefits

GHAS helps satisfy requirements for:

| Framework | Relevant GHAS Features |
|-----------|----------------------|
| **SOC 2** | Code scanning, secret scanning, audit logs |
| **ISO 27001** | Vulnerability management, dependency tracking |
| **PCI DSS** | Code review (6.3.2), vulnerability scanning (11.3) |
| **HIPAA** | Access controls, audit trails |
| **FedRAMP** | Continuous monitoring, vulnerability remediation |
| **GDPR** | Data protection by design, breach prevention |

**Auditors love:**
- Automated evidence collection
- Historical trend data
- Proof of remediation
- SBOM generation for software inventory

---

## 🚀 Getting Started with GHAS

### Step 1: Start a Trial
```bash
echo "https://github.com/enterprise/trial"
```

GitHub offers a 30-day free trial of GHAS for organizations.

### Step 2: Enable on High-Risk Repos First
Don't boil the ocean. Start with:
- Customer-facing applications
- Repos with sensitive data
- Repos with external contributors

### Step 3: Measure the Impact
After 30 days, measure:
- Vulnerabilities found
- Secrets blocked
- Developer time saved
- Compliance gaps closed

### Step 4: Make the Business Case
Present to leadership:
- Cost of GHAS vs. cost of breach
- Time saved on manual review
- Compliance boxes checked
- Developer satisfaction (they love Autofix)

---

## 💬 Common Objections (And How to Address Them)

### "We already have security tools"
GHAS integrates with your existing tools via SARIF. It's not replacement—it's enhancement. Plus, native GitHub integration means developers actually use it.

### "It's too expensive"
Compare to:
- Standalone SAST tools ($100k+/year)
- Security consultants ($300/hour)
- One data breach ($4.45M average)

### "Our developers will ignore the alerts"
That's why you need:
- Rulesets (block merges until fixed)
- Copilot Autofix (make fixing easy)
- Security Campaigns (assign ownership)

### "We don't have security expertise"
You don't need it! CodeQL is maintained by GitHub's security researchers. You get world-class vulnerability detection without building a security team.

---

## 🔧 Advanced Features Worth Exploring

### SARIF Upload (Third-Party Scanners)
Already using Snyk, Checkmarx, or SonarQube? Upload results to GitHub:

```yaml
- name: Upload SARIF
  uses: github/codeql-action/upload-sarif@v3
  with:
    sarif_file: results.sarif
```

All alerts appear in the same Security tab—one unified view.

### Custom CodeQL Query Packs
Create organization-specific queries and share them:

```bash
# Initialize a query pack
codeql pack init my-org/security-queries

# Publish to GitHub Packages
codeql pack publish
```

### Webhook Notifications
Get real-time alerts in Slack, Teams, or PagerDuty:

```bash
# Create a webhook for code scanning alerts
gh api repos/$OWNER/$REPO/hooks -X POST \
  -f name=web \
  -f config[url]="https://your-webhook.com/github" \
  -f config[content_type]=json \
  -f events[]=code_scanning_alert
```

### Security Campaigns
Coordinate large-scale remediation:

1. Go to Organization → Security → Campaigns
2. Create campaign: "Q1 Critical Vulnerability Remediation"
3. Add alerts from multiple repos
4. Assign to teams with deadlines
5. Track progress in dashboard

---

## 📚 Additional Resources

- [GitHub Advanced Security Documentation](https://docs.github.com/en/enterprise-cloud@latest/code-security)
- [GHAS Pricing](https://github.com/pricing)
- [CodeQL Query Library](https://codeql.github.com/)
- [GitHub Security Lab](https://securitylab.github.com/)
- [GHAS Certification](https://resources.github.com/learn/certifications/)

---

## 🤝 Need Help?

- **Sales:** Contact your GitHub account team
- **Technical:** [GitHub Support](https://support.github.com/)
- **Community:** [GitHub Community Discussions](https://github.com/orgs/community/discussions)

---

*"Security is not a feature—it's a foundation. GHAS makes that foundation accessible to every developer."*
