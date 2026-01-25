# Prerequisites

Setup time: ~10 minutes

---

## Requirements

| Tool | Check | Install |
|------|-------|---------|
| **Git** | `git --version` | [git-scm.com](https://git-scm.com/downloads) |
| **GitHub CLI** | `gh --version` | [cli.github.com](https://cli.github.com/) |
| **jq** (optional) | `jq --version` | `brew install jq` / `apt install jq` |
| **GitHub account** | [github.com](https://github.com/signup) | Free account works |
| **VS Code** | `code --version` | [code.visualstudio.com](https://code.visualstudio.com/) |
| **CodeQL Extension** | In VS Code | [marketplace](https://marketplace.visualstudio.com/items?itemName=GitHub.vscode-codeql) |

---

## Installation

### Git

| OS | Command |
|----|---------|
| macOS | `brew install git` |
| Windows | [Download](https://git-scm.com/download/win) |
| Linux | `sudo apt install git` |

### GitHub CLI

| OS | Command |
|----|---------|
| macOS | `brew install gh` |
| Windows | `winget install GitHub.cli` |
| Linux | [See instructions](https://github.com/cli/cli/blob/trunk/docs/install_linux.md) |

After installing:
```bash
gh auth login
```

### jq (optional)

| OS | Command |
|----|---------|
| macOS | `brew install jq` |
| Windows | `winget install jqlang.jq` |
| Linux | `sudo apt install jq` |

### VS Code & CodeQL Extension

1. Install [VS Code](https://code.visualstudio.com/).
2. Open VS Code.
3. Go to the Extensions view (`Ctrl+Shift+X`).
4. Search for "CodeQL".
5. Install the **CodeQL** extension by GitHub.

---

## Verify Setup

```bash
echo "Git: $(git --version)"
echo "GitHub CLI: $(gh --version | head -1)"
echo "jq: $(jq --version 2>/dev/null || echo 'not installed')"
echo "VS Code: $(code --version 2>/dev/null || echo 'not in PATH')"
gh auth status
```

---

## Optional: GitHub Copilot

**GitHub Copilot** is not required for this workshop, but it can enhance the experience in several phases:

- **Phase 2:** Use GitHub Copilot to generate quick fixes for Code Scanning alerts
- **Phase 3:** Use GitHub Copilot Chat to refactor hardcoded credentials
- **Phase 4:** Use GitHub Copilot to understand dependency breaking changes
- **Phase 6:** Use GitHub Copilot to help write custom CodeQL queries
- **Phase 7 (Optional):** Deep dive into bulk remediation and AI-assisted workflows

If you have GitHub Copilot enabled, you'll see "Fix with GitHub Copilot" callouts throughout the guide.

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `gh: command not found` | Restart terminal after install |
| Not authenticated | Run `gh auth login` |
| `jq: command not found` | Optional - raw JSON still works |

---

[Back to main README](../README.md)
