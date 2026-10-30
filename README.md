# Library Management Application

A robust academic library management system engineered with **Java 17** featuring complete build automation, dependency management, unit testing, and packaging via **Gradle 8**. Developed for the **Practical DevOps** course (Activity 7).
A robust academic library management system engineered with **Java 17** featuring complete build automation, dependency management, unit testing, and packaging via **Gradle 8**. Developed for the **Practical DevOps** course (Activity 7).

## Features

- **Book Management**: Add, remove, search (by title, author, ISBN), and list books
- **Member Management**: Register and list library members
- **Loan Management**: Issue books (max 3 per member, 14-day loan period), return books with automatic late fine calculation (Rs. 2/day)
- **Overdue Tracking**: View active loans and overdue books per member
- **Input Validation**: Friendly error messages, sample data seeded on startup

## Tech Stack

| Component       | Technology                        |
|-----------------|-----------------------------------|
| Language        | Java 17 (Eclipse Temurin)         |
| Build Tool      | Gradle 8.10.2 (Groovy DSL)       |
| Testing         | JUnit 5 (Jupiter) + Mockito      |
| Coverage        | JaCoCo (≥70% line coverage)      |
| Logging         | SLF4J + Logback                   |
| Utilities       | Apache Commons Lang3              |
| Packaging       | Shadow plugin (fat JAR)           |
| CI/CD           | GitHub Actions                    |

## Project Structure

```
library-management/
├── build.gradle                    # Build configuration
├── settings.gradle                 # Project settings & toolchain resolver
├── gradle.properties               # Build optimization flags
├── gradlew / gradlew.bat           # Gradle wrapper scripts
├── gradle/wrapper/                  # Wrapper JAR and properties
├── src/
│   ├── main/
│   │   ├── java/com/college/library/
│   │   │   ├── Main.java           # CLI entry point
│   │   │   ├── model/              # Book, Member, Loan
│   │   │   ├── repository/         # Interfaces + in-memory implementations
│   │   │   ├── service/            # LibraryService (business logic)
│   │   │   └── exception/          # Custom exceptions
│   │   └── resources/
│   │       └── logback.xml          # Logging configuration
│   └── test/
│       └── java/com/college/library/
│           ├── model/               # Model tests
│           ├── repository/          # Repository tests
│           └── service/             # Service tests (incl. Mockito)
├── docs/                            # Project documentation
├── .github/workflows/               # CI/CD pipeline
├── .gitignore
├── LICENSE (MIT)
└── README.md
```

## Prerequisites

- **JDK 17+** (Eclipse Temurin recommended)
- **Git** 2.x+
- Gradle wrapper is included — no separate Gradle install needed

## Quick Start

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/library-management.git
cd library-management

# Build the project (compile, test, coverage check, package)
./gradlew clean build          # Linux/macOS
gradlew.bat clean build        # Windows

# Run the application interactively
./gradlew run                  # Linux/macOS
gradlew.bat run                # Windows

# Build the fat JAR and run it
./gradlew shadowJar
java -jar build/libs/library-management-1.0.0-all.jar

# Run tests only
./gradlew test

# Generate Javadoc
./gradlew javadoc
# Open: build/docs/javadoc/index.html

# Print build info
./gradlew printBuildInfo
```

## Gradle Build Lifecycle

The `./gradlew build` command triggers the following task chain:

```
compileJava → processResources → classes → jar → assemble
                                         → test → jacocoTestReport → jacocoTestCoverageVerification → check → build
```

| Task                           | Purpose                                    |
|--------------------------------|--------------------------------------------|
| `compileJava`                  | Compiles Java source files                 |
| `processResources`            | Copies resources (logback.xml) to output   |
| `classes`                      | Depends on compileJava + processResources  |
| `test`                         | Runs JUnit 5 tests                         |
| `jacocoTestReport`            | Generates HTML + XML coverage reports      |
| `jacocoTestCoverageVerification` | Fails build if coverage < 70%           |
| `jar`                          | Creates the standard JAR                   |
| `shadowJar`                    | Creates the fat JAR with dependencies      |
| `assemble`                     | Groups packaging tasks                     |
| `check`                        | Groups verification tasks (test, coverage) |
| `build`                        | assemble + check                           |

## Test Reports

After running tests, reports are available at:

- **Test Report**: `build/reports/tests/test/index.html`
- **JaCoCo Coverage**: `build/reports/jacoco/test/html/index.html`

## Documentation

See the [docs/](docs/) folder for:
- [Problem Statement](docs/problem-statement.md)
- [Project Report](docs/project-report.md)
- [Architecture](docs/architecture.md)
- [Execution Logs](docs/execution-logs.md)
- [Troubleshooting](docs/troubleshooting.md)
- [Presentation Outline](docs/presentation-outline.md)
- [Screenshots Checklist](docs/screenshots-checklist.md)
- [Contributions](docs/contributions.md)

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

## Authors

- Puneet Kaur
- Shirin Bobdey

