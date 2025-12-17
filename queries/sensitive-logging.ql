/**
 * @name Sensitive data written to log
 * @description Detects logging of variables with sensitive names like password, token, or secret
 * @kind problem
 * @problem.severity warning
 * @precision medium
 * @id workshop/sensitive-logging
 * @tags security
 *       external/cwe/cwe-532
 */

import java

// TODO: Complete this query as part of the Phase 6 exercise
// 
// Hints:
// 1. Define what makes a method a "logging method"
//    - Look for: println, print, info, debug, warn, error, log
//    - These are typically on System.out, Logger objects, etc.
//
// 2. Define what makes a variable name "sensitive"
//    - Look for: password, secret, token, key, credential, apikey
//    - Use case-insensitive matching
//
// 3. Find method calls where a sensitive variable is passed as an argument

/**
 * Predicate: Is this method a logging method?
 * 
 * TODO: Implement this predicate
 * Hint: Check if the method name is one of the common logging methods
 */
predicate isLoggingMethod(Method m) {
  // Replace 'none()' with your implementation
  // Example: m.hasName("println") or m.hasName("info") or ...
  none()
}

/**
 * Predicate: Is this variable name sensitive?
 * 
 * TODO: Implement this predicate  
 * Hint: Use regexpMatch with case-insensitive pattern
 */
predicate isSensitiveVariableName(string name) {
  // Replace 'none()' with your implementation
  // Example: name.regexpMatch("(?i).*(password|secret).*")
  none()
}

// Main query
from MethodAccess logCall, VarAccess varAccess
where
  // TODO: Add your conditions here
  // 1. logCall should be a call to a logging method
  // 2. varAccess should be an argument to that call
  // 3. The variable's name should be sensitive
  none()
select logCall, "TODO: Complete this query - see sensitive-logging.ql.solution for reference"
