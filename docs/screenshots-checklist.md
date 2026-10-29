# Lab Evaluation & Viva Screenshots Checklist

Use this checklist to capture required screenshots for your academic submission, lab record, and viva presentation.

---

| # | Item to Capture | Exact Command or Location to Open | What Should Be Visible in the Screenshot |
|---|---|---|---|
| **1** | **Environment Versions** | Run in PowerShell:<br>`java -version`<br>`javac -version`<br>`git --version`<br>`.\gradlew.bat --version` | JDK 17 (Temurin), javac 17, Git version, and Gradle 8.10.2 launcher JVM details. |
| **2** | **Full Clean Build Success** | Run in terminal:<br>`.\gradlew.bat clean build` | Terminal displaying task execution sequence and green `BUILD SUCCESSFUL in ...` message. |
| **3** | **Custom Task Output** | Run in terminal:<br>`.\gradlew.bat printBuildInfo` | Formatted box showing Project Name, Version, Group, Java Version, and Gradle Version. |
| **4** | **HTML Test Execution Report** | Open in browser:<br>`build/reports/tests/test/index.html` | Overview page showing **100% tests successful**, **52 test cases passed**, and 0 failures. |
| **5** | **JaCoCo Code Coverage Report** | Open in browser:<br>`build/reports/jacoco/test/html/index.html` | Coverage breakdown showing green bars, with service/repository/model layers > 70%. |
| **6** | **Fat JAR Execution** | Run in terminal:<br>`java -jar build/libs/library-management-1.0.0-all.jar` | Console showing sample seed data log lines, the ASCII art menu, and listing books. |
| **7** | **Git Commit Graph & History** | Run in terminal:<br>`git log --oneline --graph --all` | Tree graph displaying feature branches, conventional commits, and merge conflict resolution. |
| **8** | **GitHub Actions CI Run** *(after push)* | GitHub Web UI:<br>`https://github.com/<YOUR_USER>/library-management/actions` | Green checkmark for workflow `Gradle Build & Test` with uploaded build artifacts. |
| **9** | **Javadoc Documentation** | Open in browser:<br>`build/docs/javadoc/index.html` | Rendered Javadoc HTML pages with class listings, package documentation, and method summaries. |

