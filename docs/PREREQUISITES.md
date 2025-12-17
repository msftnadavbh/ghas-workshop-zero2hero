# Prerequisites

This guide covers everything required to participate in the workshop. Setup typically takes 10-15 minutes.

---

## Required Accounts

### GitHub Account

You will need an active GitHub account. A free account is sufficient, as all GHAS features are available for public repositories.

If you do not have a GitHub account, [create one here](https://github.com/signup).

---

## Required Tools

### 1. Git

Git is the version control system used throughout the workshop.

**Verify installation:**
```bash
git --version
```

**Install if needed:**

| OS | Command |
|----|---------|
| **macOS** | `brew install git` or [download](https://git-scm.com/download/mac) |
| **Windows** | [Download Git for Windows](https://git-scm.com/download/win) |
| **Linux (Debian/Ubuntu)** | `sudo apt install git` |
| **Linux (Fedora)** | `sudo dnf install git` |

### 2. GitHub CLI (`gh`)

The GitHub CLI provides command-line access to GitHub features, allowing us to work efficiently without switching between the terminal and browser.

**Verify installation:**
```bash
gh --version
```

**Install if needed:**

| OS | Command |
|----|---------|
| **macOS** | `brew install gh` |
| **Windows** | `winget install GitHub.cli` or [download](https://cli.github.com/) |
| **Linux (Debian/Ubuntu)** | See [official instructions](https://github.com/cli/cli/blob/trunk/docs/install_linux.md) |
| **Linux (Fedora)** | `sudo dnf install gh` |

**Authenticate after installing:**
```bash
gh auth login
```

Choose:
- GitHub.com
- HTTPS
- Yes (authenticate with your browser)

### 3. jq (Optional but Recommended)

`jq` is a command-line JSON processor. Several exercises use it to format and filter API responses.

**Verify installation:**
```bash
jq --version
```

**Install if needed:**

| OS | Command |
|----|---------|
| **macOS** | `brew install jq` |
| **Windows** | `winget install jqlang.jq` |
| **Linux (Debian/Ubuntu)** | `sudo apt install jq` |
| **Linux (Fedora)** | `sudo dnf install jq` |

---

## Quick Verification

Run this to verify everything is set up:

```bash
# Check all tools
echo "=== Checking Prerequisites ==="
echo -n "Git: " && git --version
echo -n "GitHub CLI: " && gh --version | head -1
echo -n "jq: " && (jq --version 2>/dev/null || echo "not installed (optional)")
echo -n "GitHub Auth: " && (gh auth status 2>&1 | grep -q "Logged in" && echo "authenticated ✓" || echo "not authenticated")
echo "=== Done ==="
```

**Expected output:**
```
=== Checking Prerequisites ===
Git: git version 2.x.x
GitHub CLI: gh version 2.x.x
jq: jq-1.x (or "not installed")
GitHub Auth: authenticated ✓
=== Done ===
```

---

## Troubleshooting

### "gh: command not found"

The GitHub CLI isn't in your PATH. Try:
- Restart your terminal
- On Windows, you may need to log out and back in
- Verify the installation completed successfully

### "gh auth status" shows not authenticated

Run `gh auth login` and follow the prompts. Make sure to:
1. Select "GitHub.com"
2. Select "HTTPS"
3. Complete the browser authentication

### "jq: command not found"

`jq` is optional but makes working with JSON much easier. You can:
- Install it using the commands above
- Skip exercises that use `jq` and view raw JSON instead
- Use online tools like [jqplay.org](https://jqplay.org/) to format JSON

### Permission issues on Linux

If you get permission errors installing tools:
```bash
# Use sudo for system-wide install
sudo apt install git gh jq

# Or use a package manager like Homebrew that doesn't need sudo
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

---

## Next Steps

Once all prerequisites are verified, return to the [main README](../README.md) to begin the workshop.
