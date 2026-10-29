# Problem Statement and Objectives

## Problem Statement

Libraries manage a large number of books, members, and borrowing transactions. Manual tracking of book availability, loan periods, overdue fines, and member records is error-prone and inefficient. There is a need for an automated system that streamlines these operations.

## Objectives

1. **Develop a Library Management Application** — A menu-driven console application in Java that manages books, members, and loan transactions.

2. **Automate the Build Process with Gradle** — Use Gradle to manage dependencies, compile source code, run tests, measure coverage, and package the application into a deployable JAR.

3. **Implement Dependency Management** — Demonstrate how Gradle resolves and manages third-party libraries (JUnit 5, SLF4J, Logback, Apache Commons Lang3, Mockito).

4. **Ensure Code Quality through Testing** — Write comprehensive unit tests covering happy paths, edge cases, and exception scenarios using JUnit 5 with parameterized tests and Mockito mocking.

5. **Measure Test Coverage** — Use JaCoCo to generate code coverage reports and enforce a minimum 70% line coverage threshold.

6. **Create a Deployable Artifact** — Use the Shadow plugin to produce a fat JAR that bundles all dependencies, runnable with a single `java -jar` command.

7. **Practice Version Control** — Use Git with feature branches, meaningful commits following Conventional Commits, merge conflict resolution, and a clean history.

8. **Set Up CI/CD** — Configure a GitHub Actions workflow that automatically builds, tests, and packages the application on every push.

## Expected Outcomes

- A fully functional Gradle project with automated build, test, and packaging
- Dependency management using Gradle's `implementation` and `testImplementation` scopes
- 25+ unit tests with ≥70% code coverage
- A runnable fat JAR file (`library-management-1.0.0-all.jar`)
- A clean Git history with feature branches and conflict resolution
- CI/CD pipeline via GitHub Actions

