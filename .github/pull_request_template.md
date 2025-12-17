## Security Checklist

Before merging, please confirm:

### Code Changes
- [ ] No hardcoded credentials, API keys, or secrets
- [ ] No new vulnerable dependencies added
- [ ] Input validation implemented for user-supplied data
- [ ] Output encoding applied where appropriate
- [ ] Error messages don't expose sensitive information

### Security Scanning
- [ ] CodeQL analysis passed
- [ ] Dependency review passed
- [ ] Secret scanning found no issues

### Testing
- [ ] Security-relevant tests added/updated
- [ ] Tested with malicious input where applicable

---

## Description

<!-- Describe your changes -->

## Related Issues

<!-- Link any related issues: Fixes #123 -->

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Security fix
- [ ] Documentation update
- [ ] Dependency update
