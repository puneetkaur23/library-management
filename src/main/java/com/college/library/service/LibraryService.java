package com.college.library.service;

import com.college.library.exception.*;
import com.college.library.model.Book;
import com.college.library.model.Loan;
import com.college.library.model.Member;
import com.college.library.repository.BookRepository;
import com.college.library.repository.LoanRepository;
import com.college.library.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Core business-logic service for the Library Management Application.
 * <p>
 * Manages books, members, and loan operations. Uses an injectable {@link Clock}
 * so that date-dependent logic (overdue checks, fine calculation) is fully
 * testable without waiting for real days to pass.
 * </p>
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class LibraryService {

    private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);

    /** Repository for book persistence. */
    private final BookRepository bookRepository;

    /** Repository for member persistence. */
    private final MemberRepository memberRepository;

    /** Repository for loan persistence. */
    private final LoanRepository loanRepository;

    /**
     * Injectable clock for date-dependent logic.
     * Using a Clock makes overdue and fine calculations testable
     * without waiting real time.
     */
    private final Clock clock;

    /** Auto-incrementing counter for generating unique loan IDs. */
    private final AtomicInteger loanIdCounter = new AtomicInteger(1);

    /**
     * Constructs a LibraryService with the given repositories and clock.
     *
     * @param bookRepository   the book repository
     * @param memberRepository the member repository
     * @param loanRepository   the loan repository
     * @param clock            the clock for date operations (inject Clock.fixed() in tests)
     */
    public LibraryService(BookRepository bookRepository,
                          MemberRepository memberRepository,
                          LoanRepository loanRepository,
                          Clock clock) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.clock = clock;
    }

    // ==================== BOOK OPERATIONS ====================

    /**
     * Adds a new book to the library.
     *
     * @param isbn   the ISBN of the book
     * @param title  the title of the book
     * @param author the author of the book
     * @throws IllegalArgumentException if a book with the same ISBN already exists
     */
    public void addBook(String isbn, String title, String author) {
        if (bookRepository.findByIsbn(isbn).isPresent()) {
            throw new IllegalArgumentException("A book with ISBN '" + isbn + "' already exists.");
        }
        Book book = new Book(isbn, title, author);
        bookRepository.save(book);
        logger.info("Book added: {}", book);
    }

    /**
     * Removes a book from the library by its ISBN.
     * The book must exist and not be currently on loan.
     *
     * @param isbn the ISBN of the book to remove
     * @throws BookNotFoundException     if no book with the given ISBN exists
     * @throws IllegalStateException     if the book is currently on loan
     */
    public void removeBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN '" + isbn + "' not found."));

        // Prevent removal of books that are currently loaned out
        if (!book.isAvailable()) {
            throw new IllegalStateException("Cannot remove book '" + isbn + "' — it is currently on loan.");
        }

        bookRepository.deleteByIsbn(isbn);
        logger.info("Book removed: {}", isbn);
    }

    /**
     * Searches for books by title (case-insensitive partial match).
     *
     * @param title the title keyword to search for
     * @return a list of matching books
     */
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    /**
     * Searches for books by author (case-insensitive partial match).
     *
     * @param author the author keyword to search for
     * @return a list of matching books
     */
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author);
    }

    /**
     * Searches for a book by its exact ISBN.
     *
     * @param isbn the ISBN to search for
     * @return an Optional containing the book, or empty if not found
     */
    public Optional<Book> searchByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Returns all books in the library.
     *
     * @return a list of all books
     */
    public List<Book> listAllBooks() {
        return bookRepository.findAll();
    }

    // ==================== MEMBER OPERATIONS ====================

    /**
     * Registers a new member in the library.
     *
     * @param memberId the unique member ID
     * @param name     the member's name
     * @param email    the member's email
     * @throws IllegalArgumentException if a member with the same ID already exists
     */
    public void registerMember(String memberId, String name, String email) {
        if (memberRepository.findById(memberId).isPresent()) {
            throw new IllegalArgumentException("A member with ID '" + memberId + "' already exists.");
        }
        Member member = new Member(memberId, name, email);
        memberRepository.save(member);
        logger.info("Member registered: {}", member);
    }

    /**
     * Returns all registered members.
     *
     * @return a list of all members
     */
    public List<Member> listAllMembers() {
        return memberRepository.findAll();
    }

    // ==================== LOAN OPERATIONS ====================

    /**
     * Issues a book to a member (creates a new loan).
     * <p>
     * Business rules enforced:
     * <ul>
     *   <li>The book must exist.</li>
     *   <li>The member must exist.</li>
     *   <li>The book must be available (not currently on loan).</li>
     *   <li>The member must not exceed the maximum active loan limit ({@value Loan#MAX_LOANS_PER_MEMBER}).</li>
     * </ul>
     * </p>
     *
     * @param isbn     the ISBN of the book to issue
     * @param memberId the ID of the borrowing member
     * @return the created Loan
     * @throws BookNotFoundException      if the book does not exist
     * @throws MemberNotFoundException    if the member does not exist
     * @throws BookNotAvailableException  if the book is already on loan
     * @throws LoanLimitExceededException if the member has reached the loan limit
     */
    public Loan issueBook(String isbn, String memberId) {
        // 1. Validate the book exists
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN '" + isbn + "' not found."));

        // 2. Validate the member exists
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID '" + memberId + "' not found."));

        // 3. Check book availability
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Book '" + book.getTitle() + "' is currently not available.");
        }

        // 4. Check loan limit (max 3 active loans per member)
        List<Loan> activeLoans = loanRepository.findActiveLoansByMemberId(memberId);
        if (activeLoans.size() >= Loan.MAX_LOANS_PER_MEMBER) {
            throw new LoanLimitExceededException(
                    "Member '" + memberId + "' has reached the maximum of "
                    + Loan.MAX_LOANS_PER_MEMBER + " active loans.");
        }

        // 5. Create the loan using the injectable clock for the issue date
        LocalDate issueDate = LocalDate.now(clock);
        String loanId = "LOAN-" + loanIdCounter.getAndIncrement();
        Loan loan = new Loan(loanId, isbn, memberId, issueDate);

        // 6. Mark the book as unavailable and save the loan
        book.setAvailable(false);
        bookRepository.save(book);
        loanRepository.save(loan);

        logger.info("Book issued: {} to member {}", isbn, memberId);
        return loan;
    }

    /**
     * Returns a book and calculates any overdue fine.
     * <p>
     * Fine calculation: Rs. {@value Loan#FINE_PER_DAY} per day overdue.
     * No fine is charged if the book is returned on or before the due date.
     * </p>
     *
     * @param isbn the ISBN of the book to return
     * @return the fine amount in Rupees (0.0 if not overdue)
     * @throws BookNotFoundException  if the book does not exist
     * @throws LoanNotFoundException  if the book is not currently on loan
     */
    public double returnBook(String isbn) {
        // 1. Validate the book exists
        bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book with ISBN '" + isbn + "' not found."));

        // 2. Find the active loan for this book
        Loan loan = loanRepository.findActiveLoanByBookIsbn(isbn)
                .orElseThrow(() -> new LoanNotFoundException("Book '" + isbn + "' is not currently on loan."));

        // 3. Calculate fine based on overdue days
        LocalDate returnDate = LocalDate.now(clock);
        loan.setReturnDate(returnDate);

        double fine = 0.0;
        if (returnDate.isAfter(loan.getDueDate())) {
            // Calculate the number of overdue days
            long overdueDays = ChronoUnit.DAYS.between(loan.getDueDate(), returnDate);
            fine = overdueDays * Loan.FINE_PER_DAY;
            loan.setFine(fine);
            logger.info("Book {} returned late by {} days. Fine: Rs. {}", isbn, overdueDays, fine);
        } else {
            logger.info("Book {} returned on time. No fine.", isbn);
        }

        // 4. Mark the book as available again
        Book book = bookRepository.findByIsbn(isbn).get();
        book.setAvailable(true);
        bookRepository.save(book);
        loanRepository.save(loan);

        return fine;
    }

    /**
     * Gets all active loans for a given member.
     *
     * @param memberId the member ID
     * @return a list of active loans
     * @throws MemberNotFoundException if the member does not exist
     */
    public List<Loan> getActiveLoans(String memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID '" + memberId + "' not found."));
        return loanRepository.findActiveLoansByMemberId(memberId);
    }

    /**
     * Gets all overdue books for a given member as of the current date.
     *
     * @param memberId the member ID
     * @return a list of overdue loans
     * @throws MemberNotFoundException if the member does not exist
     */
    public List<Loan> getOverdueLoans(String memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID '" + memberId + "' not found."));
        LocalDate today = LocalDate.now(clock);
        return loanRepository.findActiveLoansByMemberId(memberId).stream()
                .filter(loan -> loan.isOverdue(today))
                .collect(Collectors.toList());
    }
}

