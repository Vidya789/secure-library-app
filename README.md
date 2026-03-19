Secure Library Management Web Application
This project is a Secure Library Management System built with Spring Boot, designed with a focus on the Secure Software Development Lifecycle (SSDLC). Instead of treating security as an afterthought, this application integrates security into every phase, from design to deployment.

👥 Group Members
Tenzghe Zang 
Vidya Nakade 
Suvaranamaliya Jothibabu 
Reda Mourad

 🚀 Features
 User Management: Secure registration and login for Users and Administrators.
 Library Services: Browse available books and submit borrowing requests.
 Admin Dashboard: Privileged access to add, update, and delete books, and manage borrowing approvals.
 Role-Based Access Control (RBAC): Strict separation of duties between standard users and admins.

 🛡️ Security Implementation (SSDLC)
 1. Secure Design & Threat ModelingWe utilized the STRIDE threat model to identify and mitigate risks early in the design phase:
 Spoofing: Mitigated by strong authentication and BCrypt password hashing.
 Tampering: Prevented through backend input validation and parameter checks.
 Elevation of Privilege: Prevented by server-side role enforcement (new users default to USER role).

 2. Defensive Coding Practices
 Password Security: Passwords are never stored in plaintext; BCrypt hashing is used to resist brute-force attacks.
 Secure Error Handling: A global exception handler ensures internal system details (like stack traces) are never exposed to the user.
 Security Logging: Important events, such as administrative actions and login attempts, are logged for auditability.

 3. Security Testing (SAST & DAST)
 Static Analysis (SonarQube): Used to identify hard-coded credentials and code quality issues. We successfully moved sensitive configurations to externalized environments.
 Dynamic Analysis (OWASP ZAP): Performed on the running application to harden session handling, cookie configurations, and authentication mechanisms.

 🏗️ ArchitectureThe application follows a standard layered architecture:
 Controller Layer: Routes HTTP requests.
 Service Layer: Handles business logic and authentication.
 Repository Layer: Interacts with the H2 Database.
 Security Layer: Managed by Spring Security.

 🛠️ Proposed CI/CD Pipeline
 To ensure continuous security, we propose a pipeline that automates:
 Code Push/PR: Triggers the build.
 SAST Scanning: SonarQube analysis during the build phase.
 Deployment: To a staging environment.
 DAST Scanning: OWASP ZAP testing on the live staging app

 📝 Conclusion
 By combining threat modeling, secure coding, and automated testing tools, this project demonstrates a proactive approach to building resilient modern software.