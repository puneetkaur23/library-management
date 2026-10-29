# Execution Logs & Verification Proof

Captured during automated build and test execution on Windows 11 with Eclipse Adoptium OpenJDK 17.0.20.1 and Gradle 8.10.2.

---

## 1. Environment Verification

```powershell
PS> java -version
openjdk version "17.0.20.1" 2026-08-18
OpenJDK Runtime Environment Temurin-17.0.20.1+1 (build 17.0.20.1+1)
OpenJDK 64-Bit Server VM Temurin-17.0.20.1+1 (build 17.0.20.1+1, mixed mode, sharing)

PS> javac -version
javac 17.0.20.1

PS> git --version
git version 2.55.0.windows.5

PS> .\gradlew.bat --version
------------------------------------------------------------
Gradle 8.10.2
------------------------------------------------------------

Build time:    2024-09-23 21:28:39 UTC
Revision:      41457813a378d3a776c5b6ff8d511110e5d16c52

Kotlin:        1.9.24
Groovy:        3.0.22
Ant:           Apache Ant(TM) version 1.10.14
Launcher JVM:  17.0.20.1 (Eclipse Adoptium 17.0.20.1+1)
Daemon JVM:    C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot (no JVM info available)
OS:            Windows 11 10.0 amd64
```

---

## 2. Custom Task Execution (`printBuildInfo`)

```text
> Task :printBuildInfo
==============================
 Build Information
==============================
 Project Name   : library-management
 Version        : 1.0.0
 Group          : com.college.library
 Java Version   : 17
 Gradle Version : 8.10.2
==============================

BUILD SUCCESSFUL in 7s
```

---

## 3. Test Execution (`.\gradlew.bat test`)

```text
> Task :compileJava
> Task :processResources
> Task :classes
> Task :compileTestJava
> Task :processTestResources NO-SOURCE
> Task :testClasses
> Task :test

ModelTest > Book: blank author throws IllegalArgumentException PASSED
ModelTest > Member: members with same ID are equal PASSED
ModelTest > Book: setAvailable toggles availability PASSED
ModelTest > Member: blank ID throws IllegalArgumentException PASSED
ModelTest > Book: toString returns formatted string PASSED
ModelTest > Member: valid construction sets all fields correctly PASSED
ModelTest > Loan: construction calculates due date correctly PASSED
ModelTest > Book: books with different ISBNs are not equal PASSED
ModelTest > Book: blank title throws IllegalArgumentException PASSED
ModelTest > Book: books with same ISBN are equal PASSED
ModelTest > Book: valid construction sets all fields correctly PASSED
ModelTest > Loan: constants have expected values PASSED
ModelTest > Loan: isOverdue returns false after return PASSED
ModelTest > Loan: isOverdue returns true one day after due date PASSED
ModelTest > Book: blank ISBN throws IllegalArgumentException > Book: blank ISBN "null" throws IllegalArgumentException PASSED
ModelTest > Book: blank ISBN throws IllegalArgumentException > Book: blank ISBN "" throws IllegalArgumentException PASSED
ModelTest > Book: blank ISBN throws IllegalArgumentException > Book: blank ISBN "  " throws IllegalArgumentException PASSED
ModelTest > Book: blank ISBN throws IllegalArgumentException > Book: blank ISBN "	" throws IllegalArgumentException PASSED
ModelTest > Loan: isOverdue returns false on due date PASSED

RepositoryTest > BookRepo: save and findByIsbn returns the book PASSED
RepositoryTest > BookRepo: deleteByIsbn removes the book PASSED
RepositoryTest > BookRepo: findByTitleContaining performs case-insensitive search PASSED
RepositoryTest > LoanRepo: findActiveLoanByBookIsbn returns active loan for book PASSED
RepositoryTest > LoanRepo: findActiveLoansByMemberId returns only active loans PASSED
RepositoryTest > BookRepo: findByAuthorContaining performs case-insensitive search PASSED
RepositoryTest > BookRepo: findByIsbn returns empty for unknown ISBN PASSED

LibraryServiceTest > getActiveLoans: returns only active loans for the member PASSED
LibraryServiceTest > removeBook: non-existent book throws BookNotFoundException PASSED
LibraryServiceTest > returnBook: book not on loan throws LoanNotFoundException PASSED
LibraryServiceTest > issueBook: successfully issues a book and marks it unavailable PASSED
LibraryServiceTest > returnBook: return on due date has zero fine PASSED
LibraryServiceTest > returnBook: 1 day late incurs Rs.2 fine PASSED
LibraryServiceTest > getOverdueLoans: detects overdue loans correctly PASSED
LibraryServiceTest > returnBook: non-existent book throws BookNotFoundException PASSED
LibraryServiceTest > issueBook: unavailable book throws BookNotAvailableException PASSED
LibraryServiceTest > getActiveLoans: unknown member throws MemberNotFoundException PASSED
LibraryServiceTest > addBook: successfully adds a new book PASSED
LibraryServiceTest > returnBook: parameterized fine calculation > 1 day(s) late ? fine Rs.2.0 PASSED
LibraryServiceTest > returnBook: parameterized fine calculation > 5 day(s) late ? fine Rs.10.0 PASSED
LibraryServiceTest > returnBook: parameterized fine calculation > 10 day(s) late ? fine Rs.20.0 PASSED
LibraryServiceTest > returnBook: parameterized fine calculation > 30 day(s) late ? fine Rs.60.0 PASSED
LibraryServiceTest > listAllBooks: returns all books PASSED
LibraryServiceTest > searchByTitle: finds books by partial title match PASSED
LibraryServiceTest > removeBook: book on loan throws IllegalStateException PASSED
LibraryServiceTest > registerMember: successfully registers a new member PASSED
LibraryServiceTest > removeBook: successfully removes an available book PASSED
LibraryServiceTest > issueBook: non-existent book throws BookNotFoundException PASSED
LibraryServiceTest > addBook: duplicate ISBN throws IllegalArgumentException PASSED
LibraryServiceTest > Mockito: issueBook calls repository methods correctly PASSED
LibraryServiceTest > returnBook: on-time return has zero fine PASSED
LibraryServiceTest > issueBook: non-existent member throws MemberNotFoundException PASSED
LibraryServiceTest > issueBook: exceeding loan limit throws LoanLimitExceededException PASSED
LibraryServiceTest > registerMember: duplicate ID throws IllegalArgumentException PASSED

> Task :jacocoTestReport

BUILD SUCCESSFUL in 12s
5 actionable tasks: 5 executed
```

---

## 4. JaCoCo Coverage Breakdown (Excluding Presentation Layer)

| Package | Covered Lines | Total Lines | Line Coverage % | Branch Coverage % |
|---|---|---|---|---|
| `com.college.library.service` | 76 | 77 | **98.7%** | **100.0%** |
| `com.college.library.repository` | 31 | 33 | **93.9%** | **70.0%** |
| `com.college.library.exception` | 10 | 10 | **100.0%** | **100.0%** |
| `com.college.library.model` | 64 | 82 | **78.0%** | **57.1%** |
| **Domain Logic Aggregate** | **181** | **202** | **89.6%** | **81.0%** |

*Coverage verification rule: Line coverage >= 70% passed successfully during `./gradlew check`.*

---

## 5. Shadow Jar Execution (`java -jar build/libs/library-management-1.0.0-all.jar`)

```text
=== Seeding sample data ===
21:18:15.937 [main] INFO  c.c.library.service.LibraryService - Book added: Book{isbn='978-0-13-468599-1', title='The Pragmatic Programmer', author='David Thomas', available=true}
21:18:15.940 [main] INFO  c.c.library.service.LibraryService - Book added: Book{isbn='978-0-201-63361-0', title='Design Patterns', author='Gang of Four', available=true}
21:18:15.940 [main] INFO  c.c.library.service.LibraryService - Book added: Book{isbn='978-0-596-00712-6', title='Head First Design Patterns', author='Eric Freeman', available=true}
21:18:15.940 [main] INFO  c.c.library.service.LibraryService - Book added: Book{isbn='978-0-13-235088-4', title='Clean Code', author='Robert C. Martin', available=true}
21:18:15.941 [main] INFO  c.c.library.service.LibraryService - Book added: Book{isbn='978-0-201-61622-4', title='The Mythical Man-Month', author='Frederick Brooks', available=true}
21:18:15.941 [main] INFO  c.c.library.service.LibraryService - Member registered: Member{id='M001', name='Aarav Sharma', email='aarav@college.edu'}
21:18:15.941 [main] INFO  c.c.library.service.LibraryService - Member registered: Member{id='M002', name='Priya Patel', email='priya@college.edu'}
21:18:15.941 [main] INFO  c.c.library.service.LibraryService - Member registered: Member{id='M003', name='Rohan Gupta', email='rohan@college.edu'}
Sample data loaded: 5 books, 3 members.

╔══════════════════════════════════════════╗
║     LIBRARY MANAGEMENT SYSTEM            ║
╠══════════════════════════════════════════╣
║  1. Add a Book                           ║
║  2. Remove a Book                        ║
║  3. Search Books                         ║
║  4. List All Books                       ║
║  5. Register a Member                    ║
║  6. List All Members                     ║
║  7. Issue a Book                         ║
║  8. Return a Book                        ║
║  9. View Active Loans                    ║
║ 10. View Overdue Books                   ║
║  0. Exit                                 ║
╚══════════════════════════════════════════╝
Enter your choice: 4

--- All Books (5) ---
ISBN                 Title                               Author                    Available 
------------------------------------------------------------------------------------------
978-0-201-61622-4    The Mythical Man-Month              Frederick Brooks          Yes       
978-0-201-63361-0    Design Patterns                     Gang of Four              Yes       
978-0-13-235088-4    Clean Code                          Robert C. Martin          Yes       
978-0-13-468599-1    The Pragmatic Programmer            David Thomas              Yes       
978-0-596-00712-6    Head First Design Patterns          Eric Freeman              Yes       

Enter your choice: 0
Thank you for using the Library Management System. Goodbye!
21:18:15.952 [main] INFO  com.college.library.Main - Application terminated.
```
