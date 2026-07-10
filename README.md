

# Spring Boot Production-Grade Template

This repository provides a **fully production-ready Spring Boot template** designed for teams who want to bootstrap new microservices with consistent standards, quality, and best practices already built in.

Use this template to avoid repeating setup work for every new service and to ensure all microservices follow the same structure, coding style, observability, logging, and tooling.

---

## 🚀 Purpose of This Template

This template is meant to help developers:

- Quickly start new Spring Boot microservices
- Maintain consistent project structure and configurations
- Enforce strict code quality from day one
- Provide ready‑to‑use logging, tracing, Swagger, and runtime configs
- Bootstrap clean environments for local development and production

Any developer should be able to clone this repo, rename the package, update the service name, and immediately start building business features — without worrying about boilerplate setup.

---

## 📦 What's Included

### ✅ Production-Ready Dependencies
- Spring Boot Actuator
- Validation (Jakarta)
- Flyway DB migrations
- Logstash JSON logging encoder
- H2 for local development

### ✅ Static Code Quality (Strict Mode)
Configured to **fail the build on violations**:
- Spotless (Google Java Format)
- Checkstyle (Google Rules + custom anti-patterns)
- PMD (custom ruleset)
- SpotBugs (max effort)
- `.editorconfig` included

### ✅ Runtime & Framework Configuration
- Configurable base context path
- Virtual threads enabled (Java 21+)
- Database configs split by profile
- JPA & Hibernate tuned for production safety
- Dynamic service name binding to Logback
- Pre‑wired application.yml structure

### ✅ Developer Experience
- Global CORS configuration (works with/without Spring Security)
- Ready-to-use `CorsConfig`
- Simple `HelloController` (once you add Step 3)
- Swagger (if enabled in later steps)

### ✅ Logging & Observability
- Async Logback JSON logs
- MDC trace & span identifiers
- Structured log format compatible with ELK/Loki/Cloud solutions

---

## 🧩 Project Structure (Standardized)

```
src/
 └── main/
     ├── java/com/example/app
     │     ├── Application.java
     │     ├── config/
     │     │     └── CorsConfig.java
     │     ├── controller/
     │     ├── service/
     │     ├── repository/
     │     └── model/
     └── resources/
           ├── application.yml
           ├── logback-spring.xml
           ├── db/migration/ (Flyway)
           └── static/
```

---

## 🛠 How to Use This Template

### 1️⃣ Create a New Project From This Template

**Option A — GitHub Template**
1. Click **Use this template**
2. Create a new repository from it
3. Clone your new service repo

**Option B — Manual Clone**
```bash
git clone https://github.com/YOUR_ORG/springboot-prod-template new-service
cd new-service
rm -rf .git
git init
```

---

### 2️⃣ Update Package & Service Name

**Rename package (IntelliJ recommended):**
`com.dev.org` → Your org + service name

**Update service name in:**
- `application.yml` (`spring.application.name`)
- `logback-spring.xml` (dynamic lookup already supported)

---

### 3️⃣ Verify Template Baseline (Required)
Run:
```bash
./gradlew clean build
./gradlew bootRun
```

Check:
- Build is **successful**
- App runs with no errors
- Health endpoint is UP:
  ```
  http://localhost:8080/actuator/health
  ```

---

## 🧪 Quality Gates

Every commit runs:
```bash
./gradlew clean check
```

Build fails if:
- Formatting is incorrect (Spotless)
- Checkstyle rules violated
- PMD rules violated
- SpotBugs finds problems

This ensures all microservices built from this template stay clean and consistent.

---

## 🎯 When to Use This Template

Use this template when:
- Starting a new microservice
- Spinning up prototypes
- Building production-ready backend services
- Wanting consistent patterns across services
- Reducing setup/boilerplate time

---

## 🎉 Contributing to This Template

If improving:
- Add a new step into `project_setup.md`
- Ensure TL;DR rules are followed
- Confirm build/run/health checks pass
- Commit with clear step reference

---

## 📄 License

This template is open for your organization or team to extend and evolve.

---

## 📬 Need Help?

Feel free to open issues, suggest improvements, or evolve this template for your use-case.

Happy building! 🚀
