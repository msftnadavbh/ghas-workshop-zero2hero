#!/bin/bash
# Security Report Script
# Generates a comprehensive security report for a GitHub repository

set -e

# Check for GitHub CLI
if ! command -v gh &> /dev/null; then
    echo "Error: GitHub CLI (gh) is not installed."
    exit 1
fi

# Configuration
REPO="${1:-ghas-workshop}"
OWNER="${2:-$(gh api user --jq '.login')}"
OUTPUT_FORMAT="${3:-text}"  # text or markdown

# Colors for terminal output
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Functions
print_header() {
    if [ "$OUTPUT_FORMAT" == "markdown" ]; then
        echo "# Security Report: $OWNER/$REPO"
        echo ""
        echo "**Generated:** $(date -u '+%Y-%m-%d %H:%M:%S UTC')"
        echo ""
        echo "---"
        echo ""
    else
        echo "========================================"
        echo "Security Report: $OWNER/$REPO"
        echo "Generated: $(date -u '+%Y-%m-%d %H:%M:%S UTC')"
        echo "========================================"
        echo ""
    fi
}

get_code_scanning_alerts() {
    local critical=$(gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '[.[] | select(.rule.security_severity_level=="critical") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local high=$(gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '[.[] | select(.rule.security_severity_level=="high") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local medium=$(gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '[.[] | select(.rule.security_severity_level=="medium") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local low=$(gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '[.[] | select(.rule.security_severity_level=="low") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local total=$((critical + high + medium + low))

    if [ "$OUTPUT_FORMAT" == "markdown" ]; then
        echo "## Code Scanning Alerts"
        echo ""
        echo "| Severity | Count |"
        echo "|----------|-------|"
        echo "| Critical | $critical |"
        echo "| High | $high |"
        echo "| Medium | $medium |"
        echo "| Low | $low |"
        echo "| **Total** | **$total** |"
        echo ""
    else
        echo "Code Scanning Alerts:"
        echo "  Critical: $critical"
        echo "  High:     $high"
        echo "  Medium:   $medium"
        echo "  Low:      $low"
        echo "  Total:    $total"
        echo ""
    fi
}

get_secret_scanning_alerts() {
    local open=$(gh api repos/$OWNER/$REPO/secret-scanning/alerts --jq '[.[] | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local resolved=$(gh api repos/$OWNER/$REPO/secret-scanning/alerts --jq '[.[] | select(.state=="resolved")] | length' 2>/dev/null || echo "0")
    local total=$((open + resolved))

    if [ "$OUTPUT_FORMAT" == "markdown" ]; then
        echo "## Secret Scanning Alerts"
        echo ""
        echo "| Status | Count |"
        echo "|--------|-------|"
        echo "| Open | $open |"
        echo "| Resolved | $resolved |"
        echo "| **Total** | **$total** |"
        echo ""
    else
        echo "Secret Scanning Alerts:"
        echo "  Open:     $open"
        echo "  Resolved: $resolved"
        echo "  Total:    $total"
        echo ""
    fi
}

get_dependabot_alerts() {
    local critical=$(gh api repos/$OWNER/$REPO/dependabot/alerts --jq '[.[] | select(.security_vulnerability.severity=="critical") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local high=$(gh api repos/$OWNER/$REPO/dependabot/alerts --jq '[.[] | select(.security_vulnerability.severity=="high") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local medium=$(gh api repos/$OWNER/$REPO/dependabot/alerts --jq '[.[] | select(.security_vulnerability.severity=="medium") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local low=$(gh api repos/$OWNER/$REPO/dependabot/alerts --jq '[.[] | select(.security_vulnerability.severity=="low") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local total=$((critical + high + medium + low))

    if [ "$OUTPUT_FORMAT" == "markdown" ]; then
        echo "## Dependabot Alerts"
        echo ""
        echo "| Severity | Count |"
        echo "|----------|-------|"
        echo "| Critical | $critical |"
        echo "| High | $high |"
        echo "| Medium | $medium |"
        echo "| Low | $low |"
        echo "| **Total** | **$total** |"
        echo ""
    else
        echo "Dependabot Alerts:"
        echo "  Critical: $critical"
        echo "  High:     $high"
        echo "  Medium:   $medium"
        echo "  Low:      $low"
        echo "  Total:    $total"
        echo ""
    fi
}

print_summary() {
    local code_critical=$(gh api repos/$OWNER/$REPO/code-scanning/alerts --jq '[.[] | select(.rule.security_severity_level=="critical") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local dep_critical=$(gh api repos/$OWNER/$REPO/dependabot/alerts --jq '[.[] | select(.security_vulnerability.severity=="critical") | select(.state=="open")] | length' 2>/dev/null || echo "0")
    local secret_open=$(gh api repos/$OWNER/$REPO/secret-scanning/alerts --jq '[.[] | select(.state=="open")] | length' 2>/dev/null || echo "0")
    
    local total_critical=$((code_critical + dep_critical))

    if [ "$OUTPUT_FORMAT" == "markdown" ]; then
        echo "---"
        echo ""
        echo "## Summary"
        echo ""
        if [ "$total_critical" -gt 0 ]; then
            echo "**Action Required:** $total_critical critical severity issues need immediate attention."
        elif [ "$secret_open" -gt 0 ]; then
            echo "**Warning:** $secret_open secret scanning alerts are open and may require credential rotation."
        else
            echo "No critical issues found. Continue monitoring and addressing lower severity items."
        fi
        echo ""
        echo "---"
        echo "*Report generated automatically*"
    else
        echo "========================================"
        echo "Summary"
        echo "========================================"
        if [ "$total_critical" -gt 0 ]; then
            echo -e "${RED}ACTION REQUIRED: $total_critical critical severity issues${NC}"
        elif [ "$secret_open" -gt 0 ]; then
            echo -e "${YELLOW}WARNING: $secret_open open secret alerts${NC}"
        else
            echo -e "${GREEN}No critical issues found${NC}"
        fi
        echo ""
    fi
}

# Main
main() {
    print_header
    get_code_scanning_alerts
    get_secret_scanning_alerts
    get_dependabot_alerts
    print_summary
}

# Run
main
