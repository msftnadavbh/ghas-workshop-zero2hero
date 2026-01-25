# Facilitator Guide

*Created by **Nadav Ben Haim***

---

## Pre-Workshop

**One week before:**
- [ ] Test template repo works
- [ ] Verify vulnerabilities trigger alerts
- [ ] Prepare completed sample repo for demos

**Day of:**
- [ ] Check GitHub status page
- [ ] Have CLI authenticated
- [ ] Open Participant Guide

---

## Timing

| Phase | Duration | Milestone |
|-------|----------|-----------|
| Setup | 10 min | Repos created |
| Phase 1 | 30 min | SBOM generated |
| Phase 2 | 35 min | Alert fixed |
| Phase 3 | 30 min | Push blocked |
| Phase 4 | 35 min | Dependabot configured |
| Phase 5 | 35 min | Ruleset created |
| Phase 6 | 40 min | Report running |
| **Phase 7** *(optional)* | 25 min | Autofix applied |
| **Phase 8** *(optional)* | 30 min | Security Overview explored |

**Total: ~4 hours (core) + 1 hour (optional phases)**

**Workshop Formats:**
- **Full day:** All 8 phases with breaks
- **Half-day:** Phases 1-4 (core features)
- **90 min intro:** Phases 1-2 (dependencies + code scanning)
- **Advanced:** Phases 4-6 + 7-8 (automation & enterprise)
- **Enterprise focus:** Phases 5-8 (governance & scale)

---

## Common Issues

| Problem | Solution |
|---------|----------|
| `gh: command not found` | Install GitHub CLI or demo via browser |
| Code scanning unavailable | Repo must be public or have GHAS license |
| Scan takes too long | Review vulnerable code while waiting (3-5 min) |
| Push not blocked | Must use CLI, not web editor |
| No Dependabot PRs | Wait 5-10 min |
| SBOM empty | Dependency graph needs time; return later |
| GitHub Copilot Autofix not showing | Requires GitHub Copilot + GHAS; skip Phase 7 if unavailable |
| Security Overview empty | Requires org-level access; demo with screenshots |
| Direct push blocked (Phase 6.3) | Ruleset working! Use PR workflow as documented |

---

## Key Points by Phase

**Phase 1 - SBOM:**
- Transitive dependencies multiply direct ones
- Typical apps have hundreds of packages

**Phase 2 - Code Scanning:**
- Static analysis finds bugs without running code
- Show data flow visualization in alerts
- **Fix with GitHub Copilot:** After participants complete manual fix, show how GitHub Copilot Autofix generates the same solution in seconds. Emphasize: "Understand the fix first, then see how AI accelerates it."

**Phase 3 - Secrets:**
- Prevention > detection
- Demo push protection live - it's impactful
- **Fix with GitHub Copilot:** Show how GitHub Copilot Chat can refactor hardcoded credentials to environment variables. Good discussion: "Would you trust AI to handle your secrets configuration?"

**Phase 4 - Dependabot:**
- Automation reduces remediation time
- Walk through dependabot.yml options
- **Fix with GitHub Copilot:** Demonstrate how GitHub Copilot can explain breaking changes in dependency updates. Useful for complex migrations where changelog reading is time-consuming.

**Phase 5 - Scale:**
- Individual repo management doesn't scale
- Rulesets enforce standards automatically

**Phase 6 - API:**
- API enables external integrations
- CodeQL has learning curve - solutions are OK
- **Fix with GitHub Copilot:** Show how GitHub Copilot can help write CodeQL queries. Great for teams new to CodeQL syntax - lowers the barrier to custom security rules.

**Phase 7 - GitHub Copilot Autofix (Optional):**
- AI accelerates remediation, not replaces review
- Always verify AI-generated fixes before merging
- Great demo: show "Generate fix" → instant PR
- If GitHub Copilot unavailable, discuss the concept and show screenshots
- **Note:** With "Fix with GitHub Copilot" sections now in Phases 2-4 and 6, Phase 7 serves as a deeper dive into bulk remediation workflows

**Phase 8 - Security Overview (Optional):**
- Enterprise perspective - managing 100s of repos
- Coverage gaps are eye-opening for security teams
- Security Configurations = "golden path" for orgs
- Private vulnerability reporting builds trust with researchers

---

## Discussion Prompts

1. "How many dependencies did you expect vs. find?"
2. "What's the business impact of SQL injection?"
3. "What's the cost of rotating leaked credentials?"
4. "How often do you update dependencies?"
5. "What would you automate with the API?"
6. "Would you trust AI to fix your security vulnerabilities?" *(Phases 2-4, 6, 7)*
7. "How do you currently track security across all your repos?" *(Phase 8)*
8. "What compliance frameworks does your org follow?" *(leads to EXTRAS.md)*
9. "When should you use manual fixes vs. GitHub Copilot?" *(Understanding vs. speed)*

---

## Audience Adaptation

**Less technical:**
- Focus on dashboards and browser UI
- Emphasize business impact
- Copy-paste is fine

**More technical:**
- Encourage experimentation
- Challenge them to modify CodeQL
- Discuss API pagination and rate limits

**Mixed:**
- Pair technical with non-technical
- Phase 5 works as bridge topic

---

## Common Questions

**"Is this free?"**
Yes for public repos. Private repos need GHAS license. Point them to [EXTRAS.md](EXTRAS.md) for full breakdown.

**"Does this replace code review?"**
No, it complements it. Catches common patterns; humans catch business logic.

**"Performance impact?"**
Scans run in Actions, not local machines.

**"How much does GHAS cost?"**
Per-committer licensing. Point to [EXTRAS.md](EXTRAS.md) for ROI discussion - one breach costs more than years of GHAS.

**"Can GitHub Copilot Autofix be trusted?"**
It's a starting point, not final answer. Always review. Show them how to verify the fix actually resolves the alert.

**"We already have security tools"**
GHAS integrates via SARIF - unified view. Native GitHub integration means developers actually use it.

---

## Wrap-up Questions

1. Which feature would you enable first?
2. What surprised you most?
3. What's your first action item?

---

## Backup Plans

**GitHub issues:** Use screenshots and walkthrough concepts

**Finish early:** Phase 7-8, custom CodeQL, API exploration, help others

**Fall behind:** Skip optional exercises, use solutions, skip Phase 7-8

**No GitHub Copilot access:** "Fix with GitHub Copilot" sections in Phases 2-4 and 6 are optional alternatives. Skip them and use manual fixes. Skip Phase 7 or demo with screenshots.

**Personal accounts only:** Phase 8 Security Overview needs org - demo or create test org

---

## Feedback

Collect at end:
- Pace (fast/right/slow)
- Difficulty
- Most valuable phase
- What to add/remove
