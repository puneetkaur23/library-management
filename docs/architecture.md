# Architecture and Workflow

## Package Architecture

The application follows a **layered architecture** with clear separation of concerns:

```mermaid
classDiagram
    class Main {
        -LibraryService libraryService
        -Scanner scanner
        +main(String[] args)
        +run()
    }

    class LibraryService {
        -BookRepository bookRepository
        -MemberRepository memberRepository
        -LoanRepository loanRepository
        -Clock clock
        +addBook(isbn, title, author)
        +removeBook(isbn)
        +searchByTitle(title)
        +issueBook(isbn, memberId)
        +returnBook(isbn)
        +getActiveLoans(memberId)
        +getOverdueLoans(memberId)
    }

    class BookRepository {
        <<interface>>
        +save(Book)
        +findByIsbn(String)
        +findAll()
    }

    class InMemoryBookRepository {
        -Map books
    }

    class Book {
        -String isbn
        -String title
        -String author
        -boolean available
    }

    class Member {
        -String memberId
        -String name
        -String email
    }

    class Loan {
        -String loanId
        -String bookIsbn
        -String memberId
        -LocalDate issueDate
        -LocalDate dueDate
        -LocalDate returnDate
        -double fine
        +isActive()
        +isOverdue(LocalDate)
    }

    Main --> LibraryService
    LibraryService --> BookRepository
    BookRepository <|.. InMemoryBookRepository
    LibraryService --> Book
    LibraryService --> Member
    LibraryService --> Loan
```

## Build Pipeline

```mermaid
flowchart LR
    A["Source Code"] --> B["GitHub Push"]
    B --> C["compileJava"]
    C --> D["test"]
    D --> E["JaCoCo Report"]
    E --> F{"Coverage >= 70%?"}
    F -- Yes --> G["jar / shadowJar"]
    F -- No --> H["BUILD FAILED"]
    G --> I["library-management-1.0.0-all.jar"]
```

## Layer Descriptions

| Layer          | Package                           | Responsibility                                      |
|----------------|-----------------------------------|-----------------------------------------------------|
| Presentation   | `com.college.library` (Main)      | Console menu, user input/output                     |
| Service        | `com.college.library.service`     | Business logic, validation, loan rules              |
| Repository     | `com.college.library.repository`  | Data access abstraction (interface + in-memory impl)|
| Model          | `com.college.library.model`       | Domain entities (Book, Member, Loan)                |
| Exception      | `com.college.library.exception`   | Custom exception classes for error handling         |

## Design Principles

- **SOLID**: Repository interfaces decouple service from data access (Dependency Inversion)
- **Testability**: Injectable `Clock` for deterministic date-based tests
- **Separation of Concerns**: Each layer has a single, clear responsibility

