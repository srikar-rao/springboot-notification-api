# 🤖 AGENTS.md — Universal Engineering Instructions for AI Code Agents  
*A project‑agnostic, high‑rigor guide for automated coding assistants.*

---

## 🧭 Purpose  
This document defines how an AI agent should **interpret, generate, iterate, and maintain code** across any backend application.  
These rules ensure consistency, high maintainability, and production‑grade engineering quality.

---

# 1. 🚦 Core Engineering Workflow (Agent Playbook)

## 1.1 Iterative Development Loop  
1. Understand the requirement and restate assumptions.  
2. Generate minimal but complete first‑cut code.  
3. Run through self‑review:  
   - Style compliance  
   - Compilation errors  
   - API contract correctness  
   - Logical decomposition  
4. Refactor using SOLID + design patterns.  
5. Add documentation (Javadoc + Swagger).  
6. Check formatting (linter/formatter).  
7. Check static analysis (code quality tools).  
8. Produce final artifact + test cases.  

The agent should repeat steps 3–8 until the solution is clean and idiomatic.

---

# 2. 🧹 Formatting & Static Analysis Rules

## 2.1 Code Formatting  
**What:** Enforce consistent style across the codebase.  
**How:** Use the project's configured formatter (e.g., Spotless, Prettier, ESLint).  
**Why:** Readability, diff clarity, merge conflict reduction.  

**Checklist:**
- No unused imports.  
- Proper spacing, line wrapping, method ordering.  
- Enforce `final` where appropriate (immutability).  
- Run formatter before committing: `./gradlew spotlessCheck` (or equivalent).  
- Treat `.editorconfig` as the single source of truth for indentation, line length, whitespace.  
- IDEs and formatters must auto-pick `.editorconfig` defaults.  

**If a formatting rule seems off:**
- Confirm expectation in `.editorconfig` and formatter config (e.g., `spotless` block in `build.gradle`).  
- Update both simultaneously; never hand-edit around formatting rules.  

## 2.2 Static Analysis  
**What:** Detect defects before runtime (null-safety, resource leaks, concurrency bugs).  
**How:** Use the project's configured analysis tool (e.g., SpotBugs, SonarQube, Checkstyle).  
**Why:** Production reliability, security, maintainability.  

**Agent must identify and fix:**
- Null‑safety violations  
- Incorrect equals/hashCode  
- Resource leaks (unclosed streams, connections)  
- Concurrency misuses (race conditions, deadlocks)  
- Blocking calls on async runtimes  
- Inefficient collection operations  
- Bad practice exceptions  

**Workflow:**
1. Run static analysis: `./gradlew check` (or equivalent).  
2. Inspect reports under `build/reports/` (e.g., `spotbugs/`, `checkstyle/`).  
3. Review rule definitions in `config/` (e.g., `config/spotbugs/exclude.xml`, `config/checkstyle/checkstyle.xml`).  
4. Fix violations in code; avoid suppressing warnings without justification.  

---

# 3. 🧰 Code Style & Language Idioms

## 3.1 Immutability & Finality  
**What:** Prefer immutable objects and final fields.  
**How:**  
- Mark fields `final` unless mutation is required.  
- Use builder patterns for object construction.  
- Avoid mutable default parameters.  

**Why:** Thread-safety, predictability, easier testing.

## 3.2 Dependency Injection  
**What:** Wire collaborators through constructors, not singletons or service locators.  
**How:**  
- Inject all dependencies as constructor parameters.  
- Mark constructor parameters `final`.  
- Use framework DI (Spring, Guice, etc.) for lifecycle management.  

**Why:** Testability, loose coupling, explicit contracts.

## 3.3 Null Handling  
**What:** Eliminate null reference errors before production.  
**How:**  
- Use null-safety annotations (`@NonNull`, `@Nullable`).  
- Validate inputs: `Objects.requireNonNull(param, "message")`.  
- Use Optional for may-be-absent values.  
- Add Jakarta bean validation annotations: `@NotNull`, `@NotBlank`, `@Size`.  

**Why:** Fail fast, clear intent, fewer runtime errors.

## 3.4 Language & Framework Features  
**What:** Use idiomatic language constructs and framework conventions.  
**How:**  
- Java: pattern matching, records, virtual threads (Java 21+).  
- Spring Boot 3.x: declarative beans, auto-configuration, conditional registration.  
- Functional paradigms: streams, higher-order functions where appropriate.  
- Lombok: Use `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`; avoid `@Data` on domain entities.  

**Why:** Readability, performance, reduced boilerplate.

---

# 4. 🏗️ Architecture & Design Patterns

## 4.1 Single Responsibility Principle  
**What:** Each class/method has one reason to change.  
**How:**  
- Break large components into focused, named units.  
- Extract orchestration, validation, and persistence into separate collaborators.  
- Use GoF patterns: Strategy (algorithms), Template Method (lifecycle), Chain of Responsibility (ordered processing), Observer (fanout), Factory/Facade (delegation).  

**Why:** Maintainability, testability, reusability.

## 4.2 Service & Abstraction Pattern  
**What:** Define service contracts as interfaces; implement via concrete classes.  
**How:**  
- For each business logic component, create `FooService` interface + `FooServiceImpl` (or `FooServiceAdapter`).  
- Inject via DI framework; never instantiate directly.  
- Compose using constructor injection, not property injection.  
- Place all services under `application/service/`.  

**Why:** Swappability, mocking, decoupling.

## 4.3 Layered Architecture  
**What:** Organize code into distinct layers with unidirectional dependency flow.  
**How:**  

| Layer | Location | Responsibility | Examples |
|-------|----------|---|---|
| **Interface (HTTP/API)** | `interface/api`, `interface/advice`, `interface/scheduler` | Entry points, error handling, scheduling | REST controllers, exception handlers, scheduled tasks |
| **Application (Business Logic)** | `application/service`, `application/rule`, `application/specification`, `application/validation`, `application/mapper` | Orchestration, rules, transformations | Service implementations, business rules, query specs, validators, DTOs mappers |
| **Domain (Models)** | `domain/<entity-name>` | Pure domain models, value objects | Aggregates, entities, domain events |
| **Persistence (Data Access)** | `persistence/entity`, `persistence/repository`, `persistence/jdbc` | Queries, transactions, schema | JPA entities, Spring Data repos, custom JDBC queries |
| **Common (Shared)** | `common/dto`, `common/enum`, `common/constant`, `common/pojo`, `common/util` | Reusable artifacts | Request/response DTOs, enums, constants, utilities |
| **Client (External APIs)** | `client/<service-name>` | REST clients, adapters | HTTP clients, API adapters |
| **Config (Framework Setup)** | `config/` | Spring beans, properties | Configuration classes, bean definitions |

**Dependency Flow:** `interface` → `application` → `domain` → `persistence`, `common`, `client`.  
**Anti-Pattern:** Never import upward (e.g., `domain` must not import `application`).

**Why:** Clear separation, easier testing, predictable flow, maintainability.

## 4.4 API Design  
**What:** Design RESTful endpoints with clear contracts.  
**How:**  
- Use DTOs (from `common/dto`) for request/response, never expose entities.  
- Annotate all controllers with Swagger/OpenAPI (`@Operation`, `@Parameter`, `@ApiResponse`).  
- Return meaningful HTTP status codes (200, 201, 400, 404, 500).  
- Version APIs if breaking changes occur.  
- Place controllers under `interface/api/`.  

**Why:** Client clarity, automatic documentation, versioning safety.

---

# 5. 📦 Package Structure (Project Convention)

Follow this structure rigorously. Each layer is isolated and has specific responsibilities.

```
root
 └── com.organization.product
       ├── common/                          # Shared, reusable components
       │   ├── dto/                         # Request/response data transfer objects
       │   ├── enum/                        # Enumerations (status, types, etc.)
       │   ├── constant/                    # Literal constants, config defaults
       │   ├── pojo/                        # Plain old Java objects, transient models
       │   └── util/                        # Utility functions, helpers
       │
       ├── application/                     # Core business logic layer
       │   ├── service/                     # Service interfaces & implementations
       │   │   └── <domain>/                # Domain-specific services (user, payment, etc.)
       │   ├── rule/                        # Business rules, validation logic
       │   ├── specification/               # Query specifications (JPA Criteria, predicates)
       │   ├── validation/                  # Input validators, bean validation handlers
       │   └── mapper/                      # Object transformation (entity ↔ DTO)
       │
       ├── interface/                       # HTTP/external entry points
       │   ├── api/                         # REST controllers
       │   │   └── <domain>/                # Domain-specific controllers (user, payment, etc.)
       │   ├── advice/                      # Global exception handlers, interceptors
       │   └── scheduler/                   # Scheduled tasks, cron jobs
       │
       ├── domain/                          # Domain-centric pure models
       │   └── <domain-name>/               # Each domain is a sub-package (user, payment, order)
       │       ├── <Entity>.java            # Domain aggregate root or entity
       │       ├── <ValueObject>.java       # Value objects (Money, Address, etc.)
       │       └── <Event>.java             # Domain events (optional)
       │
       ├── persistence/                     # Data access layer
       │   ├── entity/                      # JPA/ORM entities
       │   │   └── <domain>/                # Organized by domain
       │   ├── repository/                  # Spring Data repositories, custom queries
       │   │   └── <domain>/                # Organized by domain
       │   └── jdbc/                        # Custom JDBC operations, complex queries
       │
       ├── client/                          # External service clients
       │   └── <service-name>/              # Service-specific client (payment-gateway, auth-service)
       │       ├── <ServiceClient>.java     # REST client
       │       └── <RequestResponse>.java   # Client-specific DTOs
       │
       ├── config/                          # Framework configuration
       │   ├── ApplicationConfig.java       # Bean definitions, Spring configuration
       │   └── <FeatureConfig>.java         # Feature-specific configs
       │
       └── Application.java                 # Main entry point
```

**Key Principles:**

1. **`common/`** holds all cross-cutting, reusable artifacts. No domain logic here.  
2. **`application/`** is the heart of business logic. Services, rules, specs, mappers, validators live here.  
3. **`interface/`** is the boundary: controllers, advice, schedulers. HTTP concerns only.  
4. **`domain/`** houses pure domain models, organized by domain concept (e.g., `domain/user/`, `domain/payment/`).  
5. **`persistence/`** isolates data access. Entities and repos grouped by domain.  
6. **`client/`** wraps external HTTP APIs. Keeps adapters isolated.  
7. **`config/`** centralizes Spring bean setup and feature toggles.  

**Domain Hierarchy Example:**

For a payments product:
```
domain/user/
  └── User.java
  └── UserId.java
  
domain/payment/
  └── Payment.java
  └── PaymentRequest.java
  └── PaymentStatus.java

persistence/entity/user/
  └── UserEntity.java

persistence/entity/payment/
  └── PaymentEntity.java

application/service/user/
  └── UserService.java
  └── UserServiceImpl.java

application/service/payment/
  └── PaymentService.java
  └── PaymentServiceImpl.java

interface/api/user/
  └── UserController.java

interface/api/payment/
  └── PaymentController.java
```

---

# 6. 🧪 Testing Architecture

## 6.1 Test Structure & Mirroring  
**What:** Mirror production package structure in test source.  
**How:**  
- Create `src/test/java/com.organization.product/<package>` for each production package.  
- Add `/setup` subpackage in each test package for test data helpers.  
- Name test data classes with `TestData` postfix (e.g., `UserTestData`, `PaymentTestData`).  

**Why:** Fast navigation, clear fixture lookup, reduced duplication.

## 6.2 Unit Testing  
**What:** Test single components in isolation (no framework, DB, network).  
**How:**  
- Use JUnit 5 + Mockito.  
- Mock all external dependencies.  
- Inject via constructor.  
- Target 80% code coverage minimum.  
- Test happy path, edge cases, and failure modes.  

**Scope:** All `application/service/`, `application/rule/`, `application/specification/`, `application/validation/`, `application/mapper/`, and any other business logic components.

**Naming:** `<Component>Test` (e.g., `UserServiceTest`, `PaymentRuleTest`).

## 6.3 Integration Testing  
**What:** Test component collaboration (with DB, messaging, external services).  
**How:**  
- Use Testcontainers for ephemeral DB, message broker instances.  
- Load full Spring context only if necessary.  
- Validate end-to-end flows: controller → service → repository → DB.  
- Use transactional rollback to keep tests isolated.  

**Scope:** Repository logic, service orchestration, multi-service workflows.

**Naming:** `<Feature>IntegrationTest` (e.g., `PaymentProcessingIntegrationTest`).

## 6.4 API/Controller Testing  
**What:** Test HTTP contract (request parsing, response serialization, status codes).  
**How:**  
- Use `@WebMvcTest` (Spring Boot test slice).  
- Mock all service dependencies via `@MockBean`.  
- Use MockMvc to perform HTTP requests and assert responses.  
- Validate status, headers, body structure, error messages.  

**Scope:** Only `interface/api/` components.

**Naming:** `<Endpoint>ControllerTest` (e.g., `UserControllerTest`).

---

# 7. 📚 Documentation Standards

## 7.1 Code Documentation  
**What:** Every public contract documents its purpose and usage.  
**How:**  
- Write one-line Javadoc for every public method: `/** Verb + what it does. */`  
- Include `@param`, `@return`, `@throws` tags where meaningful.  
- Add usage snippet beside public APIs (comments or README).  
- Document non-obvious decisions (why, not what).  

**Example:**
```java
/**
 * Processes a payment and records transaction history.
 * 
 * @param paymentRequest the payment details
 * @return a PaymentResponse with confirmation ID
 * @throws PaymentException if processor returns non-recoverable error
 */
public PaymentResponse process(PaymentRequest paymentRequest) { ... }
```

**Why:** Self-documenting code, IDE tooltips, future maintainer clarity.

## 7.2 API Documentation  
**What:** REST endpoints have clear contracts discoverable by clients.  
**How:**  
- Annotate all controllers (in `interface/api/`) with Swagger/OpenAPI (`@Operation`, `@Parameter`, `@ApiResponse`).  
- Document path, query, request body, response body, error codes.  
- Keep descriptions concise (1–2 lines).  
- Auto-generate OpenAPI spec from annotations.  

**Why:** Client onboarding, automated documentation, contract clarity.

---

# 8. 🔍 Null‑Safety & Validation

## 8.1 Compile-Time Safety  
**What:** Catch null pointer errors before runtime.  
**How:**  
- Enable null analysis in IDE and build (Java 21+ `@NonNull`, `@Nullable`).  
- Mark all parameters and fields with nullability annotations.  
- Use Optional for intentionally absent values.  

**Why:** Fail fast, clear intent, IDE warnings.

## 8.2 Runtime Validation  
**What:** Enforce invariants at entry points.  
**How:**  
- Use `Objects.requireNonNull(param, "reason")` for mandatory inputs.  
- Add Jakarta Bean Validation annotations: `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Positive`.  
- Validate in DTOs and controllers, not services.  
- Provide meaningful error messages.  
- Place custom validators in `application/validation/`.  

**Why:** Graceful rejection, clear error feedback, security.

---

# 9. 🚫 Forbidden Practices

- **Anti-Pattern:** God classes  
  **Problem:** Unclear responsibility, hard to test  
  **Alternative:** Break into focused services  

- **Anti-Pattern:** Business logic in controllers  
  **Problem:** Reusability loss, testing friction  
  **Alternative:** Move to `application/service/`  

- **Anti-Pattern:** Direct entity exposure  
  **Problem:** Tight coupling, schema leakage  
  **Alternative:** Use DTOs from `common/dto/`  

- **Anti-Pattern:** Hard-coded strings  
  **Problem:** Maintenance burden, no single source of truth  
  **Alternative:** Use `common/enum/`, `common/constant/`  

- **Anti-Pattern:** Naked SQL in services  
  **Problem:** Security risk, maintainability loss  
  **Alternative:** Use repositories or `persistence/jdbc/` with parameterized statements  

- **Anti-Pattern:** Circular dependencies  
  **Problem:** Impossible to reason about, test, deploy  
  **Alternative:** Introduce mediator, invert dependency  

- **Anti-Pattern:** Static utility abuse  
  **Problem:** Non-testable, hidden dependencies  
  **Alternative:** Inject collaborators or place in `common/util/` with static final helpers only  

- **Anti-Pattern:** Mutable shared state  
  **Problem:** Race conditions, unpredictable behavior  
  **Alternative:** Use immutable objects, thread-local storage  

- **Anti-Pattern:** Swallowing exceptions  
  **Problem:** Silent failures, debugging nightmare  
  **Alternative:** Log and re-throw or handle explicitly; leverage exceptions in `interface/advice/`. Preserve existing pattern; add new exceptions using same pattern when required.  

- **Anti-Pattern:** Domain logic in persistence entities  
  **Problem:** Entity becomes bloated; domain rules leak into ORM concerns  
  **Alternative:** Keep `persistence/entity/` as pure ORM; place logic in `domain/` and `application/service/`  

- **Anti-Pattern:** Importing entities in controllers  
  **Problem:** Direct exposure of persistence schema  
  **Alternative:** Use `common/dto/` as contract; map entities via `application/mapper/`  

---

# 10. 🧠 Agent Decision Matrix

**When the requirement is ambiguous, prefer:**

| Scenario | Decision | Rationale |
|---|---|---|
| **Where to place new logic?** | Follow package hierarchy in §5; services in `application/`, models in `domain/`, entry points in `interface/api/` | Consistency, agent navigation, dependency flow |
| **Multiple implementation paths** | Simpler, testable option | YAGNI, easier to refactor later |
| **Abstraction level** | Interface + impl over inheritance | Composition > inheritance |
| **Mutation vs. immutability** | Immutable (final fields, builders) | Thread-safety, predictability |
| **Error handling** | Checked exception vs. unchecked | Unchecked for logic errors; checked for recoverable I/O |
| **Logging level** | DEBUG for entry/exit; INFO for state changes | Observability without noise |
| **Testing** | Unit test first; integration only if needed | Speed, isolation, clarity |
| **DTO creation** | Place in `common/dto/` unless service-specific | Reusability, shared contract |
| **Entity vs. Domain Model** | Keep separate; entities in `persistence/entity/`, models in `domain/` | Schema isolation, pure logic |
| **Validation** | Bean validation in DTOs; business rules in `application/rule/` | Fail fast, separation of concerns |

---

# 11. 🔧 Allowed Tools & Versions

**Core:**
- Java 21+ (pattern matching, records, virtual threads)  
- Spring Boot 3.x+  
- JUnit 5, Mockito, AssertJ  

**Build & Quality:**
- Gradle (or Maven)  
- Spotless / Prettier (formatting)  
- SpotBugs / SonarQube (static analysis)  
- Checkstyle / ESLint (lint rules)  

**Data & Messaging:**
- JPA / Spring Data  
- JDBC (for complex queries in `persistence/jdbc/`)  
- Kafka / RabbitMQ (event-driven)  
- Testcontainers (ephemeral infra)  

**API & Docs:**
- Spring Web (REST controllers in `interface/api/`)  
- OpenAPI / Swagger (documentation)  
- Spring Validation (Jakarta Bean Validation)  
- Lombok (boilerplate reduction)  

**Do not introduce:**
- Custom annotation processors (without architectural review)  
- Dynamic proxies where static composition suffices  
- Test frameworks other than JUnit 5  

---

# 12. 📘 Change Log & Git Workflow

## 12.1 Changelog Maintenance  
**What:** Keep a running journal of work completed.  
**How:**  
- Maintain `CHANGELOG.md` in repo root.  
- After each task completion, append:
  - ISO timestamp  
  - Feature/fix summary (1–2 sentences)  
  - Files modified (list or glob)  
  - Pending next steps (if any)  

**Format:**
```markdown
## [YYYY-MM-DD HH:MM UTC]
- **feat:** Added user payment endpoint  
  - Files: `interface/api/PaymentController.java`, `application/service/PaymentService.java`, `common/dto/PaymentRequest.java`  
  - Next: Add audit logging, integration tests
```

**Why:** Continuity, debugging history, dependency tracking.

## 12.2 Git Commit Conventions  
**What:** Standardized, atomic commits that tell a story.  
**How:**  
- Use conventional commit types:
  - `feat:` New feature  
  - `fix:` Bug fix  
  - `refactor:` Internal improvement (no behavior change)  
  - `test:` Add/modify tests  
  - `docs:` Documentation or AGENTS.md updates  
  - `chore:` Cleanup, dependency updates  

- **Commit when:**
  - A feature compiles and tests pass  
  - A refactor is stable  
  - A logical unit of work is complete (not multiple per file)  

- **Message format:**
  ```
  feat(payment): add idempotency check for duplicate transactions
  
  - Check transaction hash before processing
  - Return 409 Conflict if duplicate detected
  - Fixes #123
  ```

**Why:** Clear history, bisect-friendly, easy rollback, team communication.

## 12.3 Agent Behavior Expectations  
**Before starting work:**
1. Read `CHANGELOG.md` to understand completed and pending work.  
2. Check Git history for recent commits and their intent.  
3. Ask clarifying questions if requirement conflicts with prior decisions.  

**During work:**
1. Keep commits small and atomic.  
2. Update changelog after each logical milestone.  
3. Cross-reference GitHub issues/tickets in commits.  

**After work:**
1. Verify all tests pass and quality checks pass.  
2. Review changes against AGENTS.md checklist.  
3. Update changelog with completion status.  

---

# 13. ✅ Final Checklist (Agent Self-Review)

Before submitting code, verify:

- [ ] **Style:** Formatted per project config (Spotless, Prettier, etc.)  
- [ ] **Analysis:** No SpotBugs, Checkstyle, or SonarQube violations  
- [ ] **Tests:** Unit tests written, 80%+ coverage, integration tests if applicable  
- [ ] **Documentation:** Javadoc on all public methods, OpenAPI on all endpoints  
- [ ] **Architecture:** SOLID principles applied, no circular dependencies  
- [ ] **Package Structure:** Code placed in correct layer per §5 (e.g., services in `application/`, controllers in `interface/api/`)  
- [ ] **Null Safety:** All parameters marked `@Nullable` or `@NonNull`; inputs validated  
- [ ] **Immutability:** Fields marked `final` where possible  
- [ ] **Dependency Injection:** All collaborators injected, not instantiated  
- [ ] **Layering:** Dependency flow respected (§4.3); no upward imports  
- [ ] **DTOs:** Entities not exposed; `common/dto/` used for API contracts  
- [ ] **Error Handling:** Exceptions logged with context; user-friendly messages; handlers in `interface/advice/`  
- [ ] **Commits:** Atomic, conventional format, changelog updated  

---

# 14. ✔️ Philosophy  

**Guiding Principle:** Write code that ships reliably.

- **Testable:** Inject dependencies; keep logic pure; follow layered architecture.  
- **Maintainable:** SOLID principles; single responsibility; clear naming; organized by domain.  
- **Observable:** Log intent; annotate APIs; document decisions.  
- **Immutable:** Prefer final fields; builders over setters.  
- **Layered:** `interface` → `application` → `domain` → `persistence`, never reverse.  

**The Agent's Mantra:**  
*Understand the requirement. Find the right layer. Write minimal, clean code. Test relentlessly. Document clearly. Refactor fearlessly.*

---

**This AGENTS.md is the authoritative guide for all automated code generation.  
Every output must conform to this rulebook.**
