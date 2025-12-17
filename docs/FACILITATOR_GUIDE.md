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

**Total: ~4 hours with breaks**

**Shorter versions:**
- Half-day: Phases 1-4
- 90 min intro: Phases 1-2
- Advanced: Phases 4-6

---

## Common Issues

| Problem | Solution |
|---------|----------|
| `gh: command not found` | Install GitHub CLI or demo via browser |
| Code scanning unavailable | Repo must be public |
| Scan takes too long | Review vulnerable code while waiting (3-5 min) |
| Push not blocked | Must use CLI, not web editor |
| No Dependabot PRs | Wait 5-10 min |
| SBOM empty | Dependency graph needs time; return later |

---

## Key Points by Phase

**Phase 1 - SBOM:**
- Transitive dependencies multiply direct ones
- Typical apps have hundreds of packages

**Phase 2 - Code Scanning:**
- Static analysis finds bugs without running code
- Show data flow visualization in alerts

**Phase 3 - Secrets:**
- Prevention > detection
- Demo push protection live - it's impactful

**Phase 4 - Dependabot:**
- Automation reduces remediation time
- Walk through dependabot.yml options

**Phase 5 - Scale:**
- Individual repo management doesn't scale
- Rulesets enforce standards automatically

**Phase 6 - API:**
- API enables external integrations
- CodeQL has learning curve - solutions are OK

---

## Discussion Prompts

1. "How many dependencies did you expect vs. find?"
2. "What's the business impact of SQL injection?"
3. "What's the cost of rotating leaked credentials?"
4. "How often do you update dependencies?"
5. "What would you automate with the API?"

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
Yes for public repos. Private repos need GHAS license.

**"Does this replace code review?"**
No, it complements it. Catches common patterns; humans catch business logic.

**"Performance impact?"**
Scans run in Actions, not local machines.

---

## Wrap-up Questions

1. Which feature would you enable first?
2. What surprised you most?
3. What's your first action item?

---

## Backup Plans

**GitHub issues:** Use screenshots and walkthrough concepts

**Finish early:** Custom CodeQL, API exploration, help others

**Fall behind:** Skip optional exercises, use solutions

---

## Feedback

Collect at end:
- Pace (fast/right/slow)
- Difficulty
- Most valuable phase
- What to add/remove
