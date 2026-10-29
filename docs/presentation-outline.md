# Presentation Outline: Library Management Application with Gradle Build Automation

**Course**: Practical DevOps (Activity 7)  
**Presenter**: Puneet Kaur  
**Tech Stack**: Java 17, Gradle 8.10.2, JUnit 5, Mockito, JaCoCo, Shadow Jar, GitHub Actions

---

### Slide 1: Title Slide
- **Title**: Library Management Application with Gradle Build Automation
- **Subtitle**: Practical DevOps - Activity 7
- **Presenter**: Puneet Kaur
- **Key Concepts**: Automated Builds, Dependency Management, Test Coverage, CI/CD

---

### Slide 2: Problem Statement & Objectives
- Manual library book loan and fine tracking is inefficient and prone to human errors.
- **Goals**:
  - Implement a clean, modular Java console application.
  - Automate the end-to-end development cycle using Gradle.
  - Enforce code quality using unit tests (JUnit 5 + Mockito) and coverage gates (JaCoCo).
  - Package as a standalone runnable fat JAR.

---

### Slide 3: Application Architecture
- **Layered Design**:
  - `model`: Entities (`Book`, `Member`, `Loan`) with validation.
  - `repository`: In-memory data store with search capabilities.
  - `service`: Core business logic (`issueBook`, `returnBook`, fine calculation).
  - `exception`: Domain-specific exceptions.
  - `Main`: Interactive CLI menu with graceful exit handling.
- **SOLID Highlights**: Interface segregation in repositories, Dependency Injection of `Clock` for testability.

---

### Slide 4: Gradle Build Automation & Dependency Management
- **Declarative build configuration** with Gradle Groovy DSL.
- **Dependency scopes utilized**:
  - `implementation`: Internal application runtime (Logback, Commons Lang3).
  - `testImplementation`: Testing framework compile dependencies (JUnit 5, Mockito).
  - `testRuntimeOnly`: Test execution engine (JUnit Jupiter Engine).
- **Gradle Toolchain**: Pins Java 17 with the Foojay resolver convention plugin.

---

### Slide 5: Testing Strategy & Mockito Integration
- **52 Total Tests Executed** across all layers.
- **Happy Paths**: Adding books, registering members, issuing books, returning books.
- **Edge Cases**:
  - Borrow limit exceeded (>3 active loans).
  - Attempting to issue or remove unavailable books.
  - Overdue fine calculation: on due date (Rs. 0), 1 day late (Rs. 2), parameterized days.
- **Mockito Verification**: Mocked repository layer to verify contract calls and state mutation.

---

### Slide 6: Quality Gates & Code Coverage (JaCoCo)
- **Automated Verification**: `jacocoTestCoverageVerification` bound to Gradle `check` lifecycle.
- **Coverage Gate**: 70% minimum line coverage enforced.
- **Achieved Coverage**: **89.6% line coverage** across core domain, service, and data access layers.
- HTML and XML reports generated automatically after test execution.

---

### Slide 7: Packaging & Deployment (Shadow Plugin)
- **Standard JAR vs. Fat JAR**:
  - Standard JAR lacks external dependencies, failing with `NoClassDefFoundError`.
  - Shadow plugin compiles all transitive dependencies into `library-management-1.0.0-all.jar`.
- **Zero-Dependency Execution**: Run instantly with `java -jar build/libs/library-management-1.0.0-all.jar`.

---

### Slide 8: Git Workflow & CI/CD Pipeline
- **Branching Strategy**: Dedicated feature branches (`feature/model`, `feature/repository`, `feature/service`, etc.) merged with `--no-ff`.
- **Merge Conflict Resolution**: Handled and documented real Git merge conflicts.
- **GitHub Actions Workflow**:
  - Triggers on push and pull requests.
  - Validates build and test suites on Ubuntu Linux.
  - Archives test reports, JaCoCo metrics, and fat JAR artifacts.

---

### Slide 9: Viva Highlights & Key Takeaways
- Gradle build lifecycle: `compileJava -> classes -> test -> check -> assemble -> build`.
- Importance of Gradle Wrapper (`gradlew`) for reproducible builds across developer machines.
- Role of injectable `Clock` for deterministic date testing.
- Conclusion: Fully automated, robust, and deployable DevOps workflow.
