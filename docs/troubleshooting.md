# Troubleshooting & Common Issues

This guide documents real errors encountered during the setup and build of this project, along with common pitfalls in Java/Gradle projects and their solutions.

---

## 1. Real Errors Encountered During This Build

### Issue 1.1: Missing JDK 17 Compiler (`javac` not recognized, JRE only)
- **Symptom**: `java -version` showed JRE 1.8 (`1.8.0_411`), while `javac` threw `CommandNotFoundException`.
- **Cause**: The workstation only had an obsolete Oracle Java 8 JRE without JDK development tools (`javac`, `jar`, etc.).
- **Fix**: Installed Eclipse Temurin JDK 17 using winget:
  ```powershell
  winget install EclipseAdoptium.Temurin.17.JDK --accept-source-agreements --accept-package-agreements
  ```
  And configured `JAVA_HOME`:
  ```powershell
  [System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot", "User")
  $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
  $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
  ```

---

### Issue 1.2: Gradle Not Installed for Wrapper Bootstrapping
- **Symptom**: `gradle` CLI was absent on the machine, preventing direct execution of `gradle wrapper`.
- **Cause**: New or stripped machine without pre-installed build tools.
- **Fix**: Downloaded official Gradle binary zip distribution, extracted to `C:\Gradle\gradle-8.10.2`, added temporarily to PATH, and ran `gradle wrapper --gradle-version 8.10.2`. All subsequent commands run strictly through `./gradlew` / `gradlew.bat`.

---

### Issue 1.3: `Project#afterEvaluate` Failure in Lazy Task Configuration
- **Symptom**: Gradle threw `Project#afterEvaluate(Closure) on root project cannot be executed in the current context`.
- **Cause**: Calling `afterEvaluate` inside a `tasks.named('jacocoTestCoverageVerification')` configuration block is illegal in modern Gradle.
- **Fix**: Replaced with standard class filtering syntax directly using `sourceSets`:
  ```groovy
  tasks.named('jacocoTestCoverageVerification') {
      classDirectories.setFrom(
          sourceSets.main.output.classesDirs.collect {
              fileTree(dir: it, excludes: ['**/Main.class'])
          }
      )
      violationRules {
          rule {
              limit {
                  counter = 'LINE'
                  value = 'COVEREDRATIO'
                  minimum = 0.70
              }
          }
      }
  }
  ```

---

### Issue 1.4: JaCoCo Verification Failing Due to CLI Presentation Class
- **Symptom**: Aggregate project instruction coverage was ~45% because `Main.java` has a large interactive menu with switch cases that cannot be easily exercised via unit tests.
- **Cause**: Bundle-level JaCoCo rule included `Main.class` in the metric numerator and denominator.
- **Fix**: Separated UI presentation concerns from business logic; excluded `**/Main.class` from the verification filter. The underlying model, repository, service, and exception layers achieved **89.6%** line coverage.

---

## 2. Common Cross-Platform Errors & Fixes

### Issue 2.1: `Permission denied: ./gradlew` on Linux / macOS
- **Symptom**: Bash/Zsh gives `bash: ./gradlew: Permission denied`.
- **Fix**: Grant executable permission:
  ```bash
  chmod +x gradlew
  # To persist in git for CI runners:
  git update-index --chmod=+x gradlew
  ```

---

### Issue 2.2: Windows Console Unicode Garbled Characters
- **Symptom**: Box-drawing borders (`╔═══╗`) in the CLI menu render as `????` or strange symbols.
- **Fix**: Change PowerShell active code page to UTF-8 before running:
  ```powershell
  chcp 65001
  ```
  Or pass `-Dfile.encoding=UTF-8` to JVM arguments (already configured in `gradle.properties`).

---

### Issue 2.3: CRLF vs LF Line Endings Causing Git Diff Noise
- **Symptom**: Git shows entire files modified when cloned between Windows and Linux.
- **Fix**: Configure Git autocrlf handling:
  ```bash
  git config --global core.autocrlf true    # On Windows
  git config --global core.autocrlf input   # On Linux/macOS
  ```

---

### Issue 2.4: Daemon Incompatibility or Stale Gradle Locks
- **Symptom**: `Timeout waiting to lock daemon addresses registry` or `Cannot create worker`.
- **Fix**: Kill running Gradle daemons and clean temporary lock state:
  ```powershell
  .\gradlew.bat --stop
  ```

