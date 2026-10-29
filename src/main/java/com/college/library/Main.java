package com.college.library;

import com.college.library.exception.*;
import com.college.library.model.Book;
import com.college.library.model.Loan;
import com.college.library.model.Member;
import com.college.library.repository.*;
import com.college.library.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Main entry point for the Library Management Application.
 * <p>
 * Presents an interactive console menu that allows the user to manage books,
 * members, and loans. Uses the system clock for date operations.
 * </p>
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /** The library service instance (business logic layer). */
    private final LibraryService libraryService;

    /** Scanner for reading user input from the console. */
    private final Scanner scanner;

    /**
     * Constructs the Main application with the given service and scanner.
     *
     * @param libraryService the library service
     * @param scanner        the input scanner
     */
    public Main(LibraryService libraryService, Scanner scanner) {
        this.libraryService = libraryService;
        this.scanner = scanner;
    }

    /**
     * Application entry point.
     * Initializes all layers, seeds sample data, and starts the menu loop.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize repositories (in-memory implementations)
        BookRepository bookRepo = new InMemoryBookRepository();
        MemberRepository memberRepo = new InMemoryMemberRepository();
        LoanRepository loanRepo = new InMemoryLoanRepository();

        // Use the system default clock (injectable for testing)
        Clock clock = Clock.systemDefaultZone();

        // Initialize the service layer
        LibraryService service = new LibraryService(bookRepo, memberRepo, loanRepo, clock);

        // Seed sample data for demonstration
        seedSampleData(service);

        // Create scanner and start the application
        Scanner scanner = new Scanner(System.in);
        Main app = new Main(service, scanner);
        app.run();
    }

    /**
     * Seeds the library with sample books and members for demonstration.
     *
     * @param service the library service to seed data into
     */
    private static void seedSampleData(LibraryService service) {
        System.out.println("=== Seeding sample data ===");

        // Add sample books
        service.addBook("978-0-13-468599-1", "The Pragmatic Programmer", "David Thomas");
        service.addBook("978-0-201-63361-0", "Design Patterns", "Gang of Four");
        service.addBook("978-0-596-00712-6", "Head First Design Patterns", "Eric Freeman");
        service.addBook("978-0-13-235088-4", "Clean Code", "Robert C. Martin");
        service.addBook("978-0-201-61622-4", "The Mythical Man-Month", "Frederick Brooks");

        // Register sample members
        service.registerMember("M001", "Aarav Sharma", "aarav@college.edu");
        service.registerMember("M002", "Priya Patel", "priya@college.edu");
        service.registerMember("M003", "Rohan Gupta", "rohan@college.edu");

        System.out.println("Sample data loaded: 5 books, 3 members.\n");
    }

    /**
     * Runs the main menu loop.
     * Displays options, reads user choice, and dispatches to the appropriate handler.
     */
    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            String input = readLine();

            // Handle end-of-input (e.g., piped input) gracefully
            if (input == null) {
                System.out.println("\nEnd of input detected. Exiting...");
                break;
            }

            input = input.trim();

            switch (input) {
                case "1":  addBook();           break;
                case "2":  removeBook();        break;
                case "3":  searchBooks();        break;
                case "4":  listAllBooks();       break;
                case "5":  registerMember();     break;
                case "6":  listAllMembers();     break;
                case "7":  issueBook();          break;
                case "8":  returnBook();         break;
                case "9":  viewActiveLoans();    break;
                case "10": viewOverdueBooks();   break;
                case "0":
                    running = false;
                    System.out.println("Thank you for using the Library Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 0 and 10.");
            }
        }

        logger.info("Application terminated.");
    }

    /** Prints the main menu options. */
    private void printMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     LIBRARY MANAGEMENT SYSTEM            ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Add a Book                           ║");
        System.out.println("║  2. Remove a Book                        ║");
        System.out.println("║  3. Search Books                         ║");
        System.out.println("║  4. List All Books                       ║");
        System.out.println("║  5. Register a Member                    ║");
        System.out.println("║  6. List All Members                     ║");
        System.out.println("║  7. Issue a Book                         ║");
        System.out.println("║  8. Return a Book                        ║");
        System.out.println("║  9. View Active Loans                    ║");
        System.out.println("║ 10. View Overdue Books                   ║");
        System.out.println("║  0. Exit                                 ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Enter your choice: ");
    }

    /** Reads a line from the scanner, returning null on end-of-input. */
    private String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

    // ==================== MENU HANDLERS ====================

    /** Handles adding a new book. */
    private void addBook() {
        try {
            System.out.print("Enter ISBN: ");
            String isbn = readLine();
            if (isbn == null) return;

            System.out.print("Enter Title: ");
            String title = readLine();
            if (title == null) return;

            System.out.print("Enter Author: ");
            String author = readLine();
            if (author == null) return;

            libraryService.addBook(isbn.trim(), title.trim(), author.trim());
            System.out.println("✓ Book added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    /** Handles removing a book. */
    private void removeBook() {
        try {
            System.out.print("Enter ISBN of the book to remove: ");
            String isbn = readLine();
            if (isbn == null) return;

            libraryService.removeBook(isbn.trim());
            System.out.println("✓ Book removed successfully!");
        } catch (BookNotFoundException | IllegalStateException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    /** Handles searching for books by title, author, or ISBN. */
    private void searchBooks() {
        System.out.println("Search by: 1) Title  2) Author  3) ISBN");
        System.out.print("Enter search type: ");
        String type = readLine();
        if (type == null) return;

        System.out.print("Enter search term: ");
        String term = readLine();
        if (term == null) return;

        List<Book> results;
        switch (type.trim()) {
            case "1":
                results = libraryService.searchByTitle(term.trim());
                break;
            case "2":
                results = libraryService.searchByAuthor(term.trim());
                break;
            case "3":
                Optional<Book> book = libraryService.searchByIsbn(term.trim());
                if (book.isPresent()) {
                    System.out.println("Found: " + book.get());
                } else {
                    System.out.println("No book found with ISBN: " + term.trim());
                }
                return;
            default:
                System.out.println("Invalid search type.");
                return;
        }

        if (results.isEmpty()) {
            System.out.println("No books found matching your search.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            results.forEach(b -> System.out.println("  " + b));
        }
    }

    /** Lists all books in the library. */
    private void listAllBooks() {
        List<Book> books = libraryService.listAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            System.out.println("\n--- All Books (" + books.size() + ") ---");
            System.out.printf("%-20s %-35s %-25s %-10s%n", "ISBN", "Title", "Author", "Available");
            System.out.println("-".repeat(90));
            for (Book book : books) {
                System.out.printf("%-20s %-35s %-25s %-10s%n",
                        book.getIsbn(), book.getTitle(), book.getAuthor(),
                        book.isAvailable() ? "Yes" : "No");
            }
        }
    }

    /** Handles registering a new member. */
    private void registerMember() {
        try {
            System.out.print("Enter Member ID: ");
            String id = readLine();
            if (id == null) return;

            System.out.print("Enter Name: ");
            String name = readLine();
            if (name == null) return;

            System.out.print("Enter Email: ");
            String email = readLine();
            if (email == null) return;

            libraryService.registerMember(id.trim(), name.trim(), email.trim());
            System.out.println("✓ Member registered successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    /** Lists all registered members. */
    private void listAllMembers() {
        List<Member> members = libraryService.listAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members registered.");
        } else {
            System.out.println("\n--- All Members (" + members.size() + ") ---");
            System.out.printf("%-10s %-25s %-30s%n", "ID", "Name", "Email");
            System.out.println("-".repeat(65));
            for (Member m : members) {
                System.out.printf("%-10s %-25s %-30s%n", m.getMemberId(), m.getName(), m.getEmail());
            }
        }
    }

    /** Handles issuing a book to a member. */
    private void issueBook() {
        try {
            System.out.print("Enter ISBN of the book: ");
            String isbn = readLine();
            if (isbn == null) return;

            System.out.print("Enter Member ID: ");
            String memberId = readLine();
            if (memberId == null) return;

            Loan loan = libraryService.issueBook(isbn.trim(), memberId.trim());
            System.out.println("✓ Book issued successfully!");
            System.out.println("  Loan ID: " + loan.getLoanId());
            System.out.println("  Due Date: " + loan.getDueDate());
        } catch (BookNotFoundException | MemberNotFoundException
                | BookNotAvailableException | LoanLimitExceededException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    /** Handles returning a book. */
    private void returnBook() {
        try {
            System.out.print("Enter ISBN of the book to return: ");
            String isbn = readLine();
            if (isbn == null) return;

            double fine = libraryService.returnBook(isbn.trim());
            System.out.println("✓ Book returned successfully!");
            if (fine > 0) {
                System.out.printf("  Late fine: Rs. %.2f%n", fine);
            } else {
                System.out.println("  No fine — returned on time.");
            }
        } catch (BookNotFoundException | LoanNotFoundException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    /** Displays active loans for a member. */
    private void viewActiveLoans() {
        try {
            System.out.print("Enter Member ID: ");
            String memberId = readLine();
            if (memberId == null) return;

            List<Loan> loans = libraryService.getActiveLoans(memberId.trim());
            if (loans.isEmpty()) {
                System.out.println("No active loans for member: " + memberId.trim());
            } else {
                System.out.println("\n--- Active Loans (" + loans.size() + ") ---");
                for (Loan loan : loans) {
                    System.out.println("  " + loan);
                }
            }
        } catch (MemberNotFoundException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    /** Displays overdue books for a member. */
    private void viewOverdueBooks() {
        try {
            System.out.print("Enter Member ID: ");
            String memberId = readLine();
            if (memberId == null) return;

            List<Loan> overdueLoans = libraryService.getOverdueLoans(memberId.trim());
            if (overdueLoans.isEmpty()) {
                System.out.println("No overdue books for member: " + memberId.trim());
            } else {
                System.out.println("\n--- Overdue Books (" + overdueLoans.size() + ") ---");
                for (Loan loan : overdueLoans) {
                    long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(
                            loan.getDueDate(), java.time.LocalDate.now());
                    double fine = overdueDays * Loan.FINE_PER_DAY;
                    System.out.printf("  %s | Overdue by %d day(s) | Estimated fine: Rs. %.2f%n",
                            loan, overdueDays, fine);
                }
            }
        } catch (MemberNotFoundException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
}

