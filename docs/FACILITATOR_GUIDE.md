# Facilitator Guide

## GitHub Advanced Security Workshop

*Created by **Nadav Ben Haim***

This guide provides facilitators with the tools and guidance needed to deliver the GHAS workshop effectively. It includes timing recommendations, common troubleshooting scenarios, discussion prompts, and strategies for adapting to different audiences.

---

## Pre-Workshop Checklist

### One Week Before
- [ ] Verify the template repository is public and marked as a template
- [ ] Test the "Use this template" flow with a test account
- [ ] Confirm all intentional vulnerabilities trigger expected alerts
- [ ] Ensure you have backup screenshots for each phase
- [ ] Prepare a sample repository that already has completed exercises (for demonstration)

### Day Before
- [ ] Test all GitHub CLI commands work with current API versions
- [ ] Verify GitHub Actions workflows complete successfully
- [ ] Check GitHub status page for any ongoing incidents
- [ ] Prepare printed quick-reference cards if doing in-person training

### Day Of
- [ ] Arrive 15 minutes early to set up
- [ ] Open the template repository in a browser tab
- [ ] Have GitHub CLI authenticated and ready
- [ ] Open the Participant Guide for reference

---

## Timing Overview

| Phase | Duration | Key Milestone | Buffer Time |
|-------|----------|---------------|-------------|
| Setup & Intro | 10 min | Participants have repos created | 5 min |
| Phase 1 | 30 min | SBOM generated and SECURITY.md committed | 5 min |
| Break | 10 min | - | - |
| Phase 2 | 35 min | At least one alert fixed | 5 min |
| Phase 3 | 30 min | Push protection experienced | 5 min |
| Break | 10 min | - | - |
| Phase 4 | 35 min | dependabot.yml configured | 5 min |
| Phase 5 | 35 min | Ruleset created | 5 min |
| Break | 10 min | - | - |
| Phase 6 | 40 min | Security report script working | 5 min |
| Wrap-up | 15 min | Q&A and next steps | - |

**Total: ~4.5 hours including breaks and buffer time**

For shorter sessions, consider:
- **Half-day (3 hours)**: Phases 1-4 only
- **90-minute intro**: Phases 1-2 only
- **Advanced only (2 hours)**: Phases 4-6 for experienced users

---

## Phase-by-Phase Facilitation Notes

### Phase 1: Discovering What is in Your Code (30 min)

**Key Concepts to Emphasize**
- Transitive dependencies: Direct dependencies bring their own dependencies
- Supply chain visibility: You cannot secure what you cannot see
- SBOM utility: Compliance, audits, incident response

**Common Issues**
| Problem | Solution |
|---------|----------|
| `gh: command not found` | Provide GitHub CLI installation link, or demonstrate via browser |
| SBOM returns empty | Dependency graph needs a few minutes to populate; move on and return |
| jq not installed | Use `--jq` flag with `gh api` instead, or show raw JSON |

**Discussion Prompts**
- "How many dependencies did you expect versus what you found?"
- "What happens when a vulnerability is discovered in a dependency you did not know you had?"
- "How long would it take to manually create this inventory?"

**Time Check**: At 25 minutes, ensure everyone has at least generated the SBOM

---

### Phase 2: Finding Vulnerabilities in Your Code (35 min)

**Key Concepts to Emphasize**
- Static analysis finds bugs without running code
- Default setup requires zero configuration
- Autofix saves time but always review suggestions

**Common Issues**
| Problem | Solution |
|---------|----------|
| "Code scanning not available" | Repository must be public; check visibility settings |
| Workflow takes too long | It typically takes 3-5 minutes; have participants examine the vulnerable code while waiting |
| No alerts found | Workflow may still be running; check Actions tab |
| Autofix not available | Not all rules support autofix; proceed with manual fix |

**Discussion Prompts**
- "How long would it take a human reviewer to find these issues?"
- "What is the business impact of a SQL injection vulnerability?"
- "When would you choose advanced setup over default setup?"

**Demonstration Tip**: Show the data flow visualization in the alert details page - it helps explain how the vulnerability works.

**Time Check**: At 30 minutes, ensure everyone has seen at least one alert. Fixing can continue during break if needed.

---

### Phase 3: Preventing Secrets from Leaking (30 min)

**Key Concepts to Emphasize**
- Prevention is better than detection
- Push protection catches secrets before they enter the repository
- Custom patterns extend detection to organization-specific credentials

**Common Issues**
| Problem | Solution |
|---------|----------|
| Push not blocked | Web editor handles some patterns differently; must use CLI |
| Secret alert not appearing | Secret scanning scans entire history; alert may be on a different file |
| Custom pattern not matching | Check regex syntax; Hyperscan uses PCRE subset |

**Discussion Prompts**
- "What is the cost of rotating a leaked credential?"
- "How do you handle legitimate test credentials?"
- "What happens to secrets in git history after rotation?"

**Live Demo Tip**: The push protection block is very impactful. Make sure to demonstrate this live, not just describe it.

**Time Check**: At 25 minutes, ensure everyone has experienced the push protection block

---

### Phase 4: Automating Dependency Updates (35 min)

**Key Concepts to Emphasize**
- Automation reduces mean time to remediate
- Grouped updates reduce PR noise
- Dependency review prevents new vulnerabilities

**Common Issues**
| Problem | Solution |
|---------|----------|
| No Dependabot PRs appearing | PRs take 5-10 minutes; check Settings > Code security |
| YAML syntax errors | Validate indentation; YAML is sensitive to spaces |
| Dependency review action failing | Check workflow permissions |

**Discussion Prompts**
- "How often does your team currently update dependencies?"
- "What is the risk of running outdated dependencies?"
- "How do you balance stability with security?"

**Configuration Walkthrough**: Walk through the `dependabot.yml` file line by line, explaining each option.

**Time Check**: At 30 minutes, ensure everyone has the dependabot.yml committed

---

### Phase 5: Managing Security Across Repositories (35 min)

**Key Concepts to Emphasize**
- Individual repository management does not scale
- Security Overview provides organization-wide visibility
- Rulesets enforce standards automatically

**Common Issues**
| Problem | Solution |
|---------|----------|
| Security Overview not visible | Full Security Overview requires organization account |
| Ruleset API errors | Some features require specific permissions |
| Second repo not created | Template creation may fail if name already exists |

**Discussion Prompts**
- "How do you currently track security across multiple projects?"
- "What policies would you want to enforce organization-wide?"
- "How does automation change the security team's role?"

**For Individual Accounts**: If participants only have personal accounts, focus on comparing Security tabs across two repositories rather than the full Security Overview.

**Time Check**: At 30 minutes, ensure everyone has created a ruleset

---

### Phase 6: Advanced Automation and Custom Detection (40 min)

**Key Concepts to Emphasize**
- API enables integration with external systems
- Custom queries detect organization-specific issues
- Automation compounds security investment

**Common Issues**
| Problem | Solution |
|---------|----------|
| API rate limiting | Wait a few minutes, or use authenticated requests with higher limits |
| CodeQL syntax errors | Provide the solution file; CodeQL has a learning curve |
| Workflow permission errors | Check repository settings for Actions permissions |

**Discussion Prompts**
- "What systems would you integrate with the security API?"
- "What patterns specific to your organization would you want to detect?"
- "How would you measure the success of your security program?"

**For Less Technical Participants**: It is acceptable to review the provided solutions rather than writing from scratch. The goal is understanding what is possible.

**Time Check**: At 35 minutes, ensure everyone has run at least one API query successfully

---

## Audience Adaptation

### For Less Technical Participants
- Emphasize visual outcomes (dashboards, alert pages)
- Focus on business impact and time savings
- Pair with technical participants for CLI exercises
- Copying and pasting commands without full comprehension is acceptable during the learning process
- Use more browser-based demonstrations

### For Technical Participants
- Encourage experimentation beyond the exercises
- Challenge them to modify the CodeQL query
- Discuss edge cases and limitations
- Ask them to extend the security report script
- Discuss API rate limits and pagination

### For Mixed Audiences
- Pair technical and non-technical participants
- Have technical participants explain steps to partners
- Use Phase 5 (Security Overview) as a bridge topic
- Allow advanced participants to help others

---

## Wrap-up Discussion (15 min)

### Suggested Questions
1. "Which feature would you enable first in your real projects?"
2. "What surprised you most about the detection capabilities?"
3. "How would you handle the alert backlog in a legacy codebase?"
4. "What is your first action item after this workshop?"

### Next Steps to Suggest
- Enable Dependabot on one production repository this week
- Schedule a team session to triage existing alerts
- Create a security policy for your most critical repository
- Explore the GitHub Security certification

### Resources to Share
- GitHub Security Documentation
- CodeQL Documentation
- GitHub Skills security courses
- Your organization's security team contact

---

## Handling Common Questions

**"Is this really free?"**
Yes, for public repositories. Organizations with GitHub Enterprise Cloud or GitHub Team can use these features on private repositories with the appropriate licenses.

**"How accurate is the scanning?"**
Code scanning has low false-positive rates because CodeQL understands code semantically, not just pattern matching. However, no scanner catches 100% of issues.

**"Does this replace manual code review?"**
No, it complements it. Automated scanning catches common patterns; human review catches business logic issues and context-specific problems.

**"What about performance impact?"**
Scanning runs in GitHub Actions, not on your developers' machines. Default setup optimizes for minimal CI time.

**"Can I use this with other CI systems?"**
Yes, CodeQL CLI can be integrated with any CI system. SARIF file uploads allow third-party scanner results in GitHub.

---

## Backup Plans

### If GitHub Is Experiencing Issues
- Have screenshots of each phase ready
- Walk through the concepts without live execution
- Use the sample completed repository for demonstration

### If Participants Finish Early
- Challenge exercises in each phase
- Custom CodeQL query writing
- API exploration
- Help other participants

### If Participants Fall Behind
- Use buffer time between phases
- Pair struggling participants with faster ones
- Focus on key milestones; skip optional challenges
- Provide completed solutions for copy-paste

---

## Feedback Collection

At the end of the workshop, collect feedback on:
1. Pace (too fast, just right, too slow)
2. Difficulty (too easy, appropriate, too hard)
3. Most valuable phase
4. What would you add or remove?
5. Net Promoter Score: "How likely are you to recommend this workshop?"

---

## Questions and Support

For questions about facilitating this workshop, please open an issue in the template repository.
