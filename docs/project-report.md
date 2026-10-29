# Project Report

## 1. Introduction

This project, "Library Management Application with Gradle Build Automation," is developed as part of the Practical DevOps course (Activity 7). It demonstrates how to build a Java application with automated dependency management, testing, coverage analysis, and packaging using Gradle.

## 2. Objectives

- Build a menu-driven console library management application in Java 17
- Use Gradle for build automation (compile, test, package)
- Manage dependencies using Gradle's dependency management system
- Write comprehensive unit tests with JUnit 5 and Mockito
- Measure code coverage with JaCoCo and enforce ≥70% threshold
- Package the application as a runnable fat JAR using the Shadow plugin
- Practice Git version control with feature branches and Conventional Commits
- Set up CI/CD with GitHub Actions

## 3. Tools and Technologies Used

| Tool              | Version    | Purpose                                   |
|-------------------|------------|-------------------------------------------|
| Java (Temurin)    | 17.0.20    | Programming language runtime              |
| Gradle            | 8.10.2     | Build automation tool                     |
| JUnit 5           | 5.11.3     | Unit testing framework                    |
| Mockito           | 5.14.2     | Mocking framework for unit tests          |
| JaCoCo            | (bundled)  | Code coverage analysis                    |
| SLF4J + Logback   | 2.0.16     | Logging facade and implementation         |
| Commons Lang3     | 3.17.0     | Utility library (StringUtils)             |
| Shadow Plugin     | 8.3.5      | Fat JAR packaging                         |
| Git               | 2.55.0     | Version control system                    |
| GitHub Actions    | -          | CI/CD pipeline                            |

## 4. Architecture

The application uses a layered architecture:
- **Model Layer**: Domain entities (Book, Member, Loan) with validation
- **Repository Layer**: Interface-based data access with in-memory implementations
- **Service Layer**: Business logic (issue/return books, fine calculation)
- **Presentation Layer**: Console-based menu-driven UI

Design patterns used: Repository Pattern, Dependency Injection (Clock), layered architecture following SOLID principles.

## 5. Implementation Details

### 5.1 Model Classes
- `Book`: ISBN (unique), title, author, availability status
- `Member`: ID (unique), name, email
- `Loan`: Links book and member with issue date, due date (14 days), return date, and fine

### 5.2 Business Rules
- Maximum 3 active loans per member
- Loan period: 14 days
- Late fine: Rs. 2 per overdue day
- Books cannot be removed while on loan
- Duplicate ISBNs and member IDs are rejected

### 5.3 Gradle Configuration
- Plugins: java, application, jacoco, com.gradleup.shadow
- Java toolchain: version 17 with Foojay auto-download
- Dependency scopes: implementation, testImplementation, testRuntimeOnly
- Custom task: `printBuildInfo`

## 6. Testing

- **25+ test cases** across model, repository, and service layers
- **Happy paths**: Normal operations (add, issue, return, search)
- **Edge cases**: Duplicate ISBNs, loan limits, unknown members, fine on due date vs. late
- **Parameterized tests**: Fine calculation for various overdue durations
- **Mockito tests**: Repository interaction verification
- **Coverage**: JaCoCo enforcement at ≥70% line coverage

## 7. Results

- All tests pass with `./gradlew test`
- JaCoCo coverage meets the 70% threshold
- Fat JAR runs successfully: `java -jar build/libs/library-management-1.0.0-all.jar`
- Clean Git history with feature branches and merge conflict resolution

## 8. Challenges Faced

1. **JAVA_HOME Configuration**: The system had Java 8 JRE; required installing JDK 17 and updating environment variables
2. **Gradle Wrapper**: First-time download of the wrapper distribution takes time
3. **Console Encoding**: Windows console may not render Unicode characters (menu borders) correctly — configurable via `chcp 65001`
4. **Mockito Version Compatibility**: Ensured Mockito version is compatible with Java 17

## 9. Conclusion

This project successfully demonstrates the use of Gradle for automating the software development lifecycle of a Java application. Key concepts covered include dependency management, build configuration, automated testing, code coverage enforcement, and CI/CD integration.

## 10. Future Scope

- **Persistent Storage**: Replace in-memory repositories with a database (H2, SQLite, or MySQL)
- **REST API**: Expose library operations as REST endpoints using Spring Boot
- **Web UI**: Add a frontend using Thymeleaf or React
- **Authentication**: Member login with role-based access control
- **Reports**: Generate borrowing statistics and overdue reports
- **Reservation System**: Allow members to reserve books that are currently on loan

