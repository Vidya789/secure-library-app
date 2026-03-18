# Task 3 Security Work Summary

## Overview
This document summarizes the security-related work completed for Task 3 in the Secure Library App project. The goal of these changes was to strengthen authentication, input handling, authorization, error handling, and security evidence collection in line with the final project requirements.

## Security Improvements Implemented

### 1. Secure password handling
- Registration logic was updated to hash passwords before saving them to the database.
- `PasswordEncoder` is now used so that passwords are not stored in plain text.
- This reduces the risk of credential exposure if the database is compromised.

### 2. Prevented role escalation during registration
- The registration flow was changed so that new users are always assigned the role `USER` on the server side.
- The backend no longer trusts a client-provided role during registration.
- This prevents a user from registering directly as an admin.

### 3. Backend input validation
- Validation was added for registration input such as username and password.
- Validation was also added for book-related input and request parameters where applicable.
- This ensures invalid or suspicious input is rejected by the backend even if frontend checks are bypassed.

### 4. Role-Based Access Control (RBAC)
- Admin-only routes were restricted in `SecurityConfig`.
- Sensitive operations such as adding, updating, deleting books, and approving/rejecting borrow requests are limited to admin users.
- Regular users can access only the endpoints intended for authenticated non-admin users.
- RBAC testing also identified and fixed a configuration issue where a normal user could still access an admin-only action before the final role restrictions were correctly enforced.

### 5. Added a default administrator account for testing and verification
- A default admin account was initialized in the application startup logic for security testing purposes.
- This was necessary because the project did not include a ready-to-use admin account in the existing data.
- The default admin account was used to verify admin-only routes and collect evidence for successful authorized actions.

### 6. Fixed role mapping between stored roles and Spring Security authorities
- Role mapping was updated so that stored roles such as `USER` and `ADMIN` are correctly interpreted by Spring Security.
- Authorities are now mapped in the expected `ROLE_USER` / `ROLE_ADMIN` format.
- This fixed the issue where an authenticated admin account could still receive `403 Forbidden` due to incorrect authority mapping.

### 7. Safer exception handling
- A global exception handler was added to return safe error messages.
- This avoids exposing stack traces or internal implementation details to end users.
- Common cases such as validation errors, missing resources, bad requests, and access denial are handled explicitly.

### 8. Improved stack trace and error response handling
- Error handling was refined so that unexpected internal failures and known resource errors are handled differently.
- Non-existing resources such as missing books can now return a cleaner and more meaningful error response instead of falling back to a generic internal server error.
- This improves both safety and clarity in runtime behavior.

### 9. Basic security logging
- Logging was added for important actions such as:
  - user registration
  - borrow request submission
  - admin approval/rejection
  - admin book management actions
- These logs improve traceability and provide evidence of security-aware design.

### 10. Improved frontend credential handling
- Frontend credential storage was improved so that plain-text password storage is avoided.
- This reduces unnecessary exposure of sensitive credentials on the client side.

## Main Files Updated
The following files were updated or added as part of Task 3:

- `AuthController.java`
- `UserRepository.java`
- `RegisterRequest.java`
- `SecurityConfig.java`
- `BookController.java`
- `BorrowRequestController.java`
- `GlobalExceptionHandler.java`
- `BadRequestException.java`
- `ResourceNotFoundException.java`
- `User.java`
- `Book.java`
- `LibraryappApplication.java`
- `CustomUserDetailsService.java`
- `application.properties`
- `app.js`

## Runtime Evidence Collected
The following runtime evidence is recommended for the report and presentation:

1. Registration success
2. Registration failure due to invalid input
3. Registration failure due to duplicate username
4. Before: a normal user could still perform admin-only actions
5. After: access is denied with 403 after fixing RBAC
6. Before: still getting a 403 error after adimin logging in.
7. After: Admin successfully performing an admin action
8. Safe error response for invalid or non-existing resources
9. Console logs showing security-related events

## Suggested Before / After Points for the Report

### Before
- Registration accepted raw input too directly.
- Role handling during registration was not sufficiently constrained.
- Backend validation was limited or missing.
- Admin route protection was incomplete.
- There was no ready-to-use admin account for verification.
- Role mapping between stored roles and granted authorities was not fully aligned.
- Error responses could expose too much technical detail or return an overly generic internal error.
- Security logging was limited.

### After
- Passwords are hashed before storage.
- New accounts are forced to role `USER`.
- Backend validation rejects invalid input.
- Admin-only endpoints are protected with RBAC.
- A default administrator account is available for controlled testing and verification.
- Stored roles are correctly mapped to Spring Security authorities.
- Errors return safer and cleaner messages, with clearer handling for known resource problems.
- Important security actions are logged.
