<p align="center">
  <img src="Logo.jpg" alt="GitHub Advanced Security Workshop" width="400">
</p>

# GitHub Advanced Security Workshop

### Zero to Hero

Learn GitHub Advanced Security hands-on. Work with real vulnerable code, trigger security alerts, and fix them yourself.

*Created by **Nadav Ben Haim***

---

## What is GitHub Advanced Security?

GitHub Advanced Security (GHAS) is a suite of security tools built directly into GitHub. It helps you find and fix vulnerabilities in your code, prevent secrets from leaking, and keep your dependencies up to date-all without leaving your workflow.

**This workshop covers:**

| Feature | What It Does |
|---------|--------------|
| **Dependency Graph & SBOM** | Maps all your open-source dependencies |
| **Code Scanning (CodeQL)** | Finds vulnerabilities like SQL Injection, XSS |
| **Secret Scanning** | Detects leaked API keys, tokens, passwords |
| **Push Protection** | Blocks secrets *before* they enter the repo |
| **Dependabot** | Auto-creates PRs to update vulnerable packages |
| **Rulesets** | Enforces security policies across many repos |

---

## Prerequisites

Before starting, make sure you have:

- **Git** installed (`git --version`)
- **GitHub CLI** installed and authenticated (`gh --version && gh auth status`)
- A **GitHub account** (free tier works)

Need help? See the [Prerequisites Guide](docs/PREREQUISITES.md).

---

## Get Started

**Step 1:** Create your workshop repository from this template:

```bash
gh repo create ghas-workshop --template msftnadavbh/ghas-workshop-zero2hero --public --clone
cd ghas-workshop
```

> ⚠️ Your repository **must be public** to access GHAS features for free.

**Step 2:** Open the **[Participant Guide](docs/PARTICIPANT_GUIDE.md)** and follow the exercises.

---

## Workshop Duration

| Version | Phases | Time |
|---------|--------|------|
| Full | 1–6 | ~3.5 hours |
| Half-day | 1–4 | ~2 hours |
| Intro | 1–2 | ~1 hour |

---

## Resources

- [GitHub Security Documentation](https://docs.github.com/en/code-security)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [GitHub Skills](https://skills.github.com/)

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).

**Disclaimer**: This repository contains intentionally vulnerable code for educational purposes only. Do not deploy to production.
