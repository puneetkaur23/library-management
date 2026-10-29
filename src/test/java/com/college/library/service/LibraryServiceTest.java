package com.college.library.service;

import com.college.library.exception.*;
import com.college.library.model.Book;
import com.college.library.model.Loan;
import com.college.library.model.Member;
import com.college.library.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LibraryService}.
 * <p>
 * Uses a fixed clock to make date-dependent tests deterministic.
 * Includes both real-repository tests and Mockito-mocked tests.
 * </p>
 */
class LibraryServiceTest {

    /** Fixed date used throughout tests: 2026-10-15 */
    private static final LocalDate FIXED_DATE = LocalDate.of(2026, 10, 15);

    /** A clock fixed to FIXED_DATE so all tests are deterministic. */
    private static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            ZoneId.systemDefault());

    private BookRepository bookRepo;
    private MemberRepository memberRepo;
    private LoanRepository loanRepo;
    private LibraryService service;

    @BeforeEach
    void setUp() {
        // Use real in-memory repositories for most tests
        bookRepo = new InMemoryBookRepository();
        memberRepo = new InMemoryMemberRepository();
        loanRepo = new InMemoryLoanRepository();
        service = new LibraryService(bookRepo, memberRepo, loanRepo, FIXED_CLOCK);
    }

    // ==================== BOOK OPERATIONS ====================

    @Test
    @DisplayName("addBook: successfully adds a new book")
    void addBook_success() {
        service.addBook("ISBN-1", "Clean Code", "Robert Martin");

        Optional<Book> found = service.searchByIsbn("ISBN-1");
        assertTrue(found.isPresent());
        assertEquals("Clean Code", found.get().getTitle());
    }

    @Test
    @DisplayName("addBook: duplicate ISBN throws IllegalArgumentException")
    void addBook_duplicateIsbn_throws() {
        service.addBook("ISBN-1", "Book A", "Author A");
        assertThrows(IllegalArgumentException.class,
                () -> service.addBook("ISBN-1", "Book B", "Author B"));
    }

    @Test
    @DisplayName("removeBook: successfully removes an available book")
    void removeBook_success() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.removeBook("ISBN-1");
        assertTrue(service.searchByIsbn("ISBN-1").isEmpty());
    }

    @Test
    @DisplayName("removeBook: non-existent book throws BookNotFoundException")
    void removeBook_notFound_throws() {
        assertThrows(BookNotFoundException.class,
                () -> service.removeBook("UNKNOWN"));
    }

    @Test
    @DisplayName("removeBook: book on loan throws IllegalStateException")
    void removeBook_onLoan_throws() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.registerMember("M001", "Member", "m@test.com");
        service.issueBook("ISBN-1", "M001");

        assertThrows(IllegalStateException.class,
                () -> service.removeBook("ISBN-1"));
    }

    @Test
    @DisplayName("searchByTitle: finds books by partial title match")
    void searchByTitle_partialMatch() {
        service.addBook("ISBN-1", "Clean Code", "Robert Martin");
        service.addBook("ISBN-2", "Code Complete", "Steve McConnell");
        service.addBook("ISBN-3", "Design Patterns", "GoF");

        List<Book> results = service.searchByTitle("Code");
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("listAllBooks: returns all books")
    void listAllBooks_returnsAll() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.addBook("ISBN-2", "Book B", "Author B");

        assertEquals(2, service.listAllBooks().size());
    }

    // ==================== MEMBER OPERATIONS ====================

    @Test
    @DisplayName("registerMember: successfully registers a new member")
    void registerMember_success() {
        service.registerMember("M001", "Aarav", "aarav@test.com");

        List<Member> members = service.listAllMembers();
        assertEquals(1, members.size());
        assertEquals("Aarav", members.get(0).getName());
    }

    @Test
    @DisplayName("registerMember: duplicate ID throws IllegalArgumentException")
    void registerMember_duplicateId_throws() {
        service.registerMember("M001", "Name A", "a@test.com");
        assertThrows(IllegalArgumentException.class,
                () -> service.registerMember("M001", "Name B", "b@test.com"));
    }

    // ==================== ISSUE BOOK ====================

    @Test
    @DisplayName("issueBook: successfully issues a book and marks it unavailable")
    void issueBook_success() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.registerMember("M001", "Member", "m@test.com");

        Loan loan = service.issueBook("ISBN-1", "M001");

        assertNotNull(loan);
        assertEquals("ISBN-1", loan.getBookIsbn());
        assertEquals("M001", loan.getMemberId());
        assertEquals(FIXED_DATE, loan.getIssueDate());
        assertEquals(FIXED_DATE.plusDays(14), loan.getDueDate());
        // Book should be marked unavailable
        assertFalse(service.searchByIsbn("ISBN-1").get().isAvailable());
    }

    @Test
    @DisplayName("issueBook: non-existent book throws BookNotFoundException")
    void issueBook_bookNotFound_throws() {
        service.registerMember("M001", "Member", "m@test.com");
        assertThrows(BookNotFoundException.class,
                () -> service.issueBook("UNKNOWN", "M001"));
    }

    @Test
    @DisplayName("issueBook: non-existent member throws MemberNotFoundException")
    void issueBook_memberNotFound_throws() {
        service.addBook("ISBN-1", "Book A", "Author A");
        assertThrows(MemberNotFoundException.class,
                () -> service.issueBook("ISBN-1", "UNKNOWN"));
    }

    @Test
    @DisplayName("issueBook: unavailable book throws BookNotAvailableException")
    void issueBook_bookUnavailable_throws() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.registerMember("M001", "Member A", "a@test.com");
        service.registerMember("M002", "Member B", "b@test.com");
        service.issueBook("ISBN-1", "M001"); // First borrow

        assertThrows(BookNotAvailableException.class,
                () -> service.issueBook("ISBN-1", "M002"));
    }

    @Test
    @DisplayName("issueBook: exceeding loan limit throws LoanLimitExceededException")
    void issueBook_loanLimitExceeded_throws() {
        service.registerMember("M001", "Member", "m@test.com");
        service.addBook("ISBN-1", "Book 1", "Author");
        service.addBook("ISBN-2", "Book 2", "Author");
        service.addBook("ISBN-3", "Book 3", "Author");
        service.addBook("ISBN-4", "Book 4", "Author");

        service.issueBook("ISBN-1", "M001");
        service.issueBook("ISBN-2", "M001");
        service.issueBook("ISBN-3", "M001"); // 3rd loan — at limit

        assertThrows(LoanLimitExceededException.class,
                () -> service.issueBook("ISBN-4", "M001"));
    }

    // ==================== RETURN BOOK ====================

    @Test
    @DisplayName("returnBook: on-time return has zero fine")
    void returnBook_onTime_zeroFine() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.registerMember("M001", "Member", "m@test.com");
        service.issueBook("ISBN-1", "M001");

        // Return on the same day (fixed clock) — well before due date
        double fine = service.returnBook("ISBN-1");
        assertEquals(0.0, fine);
        assertTrue(service.searchByIsbn("ISBN-1").get().isAvailable());
    }

    @Test
    @DisplayName("returnBook: return on due date has zero fine")
    void returnBook_onDueDate_zeroFine() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.registerMember("M001", "Member", "m@test.com");
        service.issueBook("ISBN-1", "M001");

        // Create a clock fixed to exactly the due date
        LocalDate dueDate = FIXED_DATE.plusDays(Loan.LOAN_PERIOD_DAYS);
        Clock dueDateClock = Clock.fixed(
                dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());

        // Create a new service with the due-date clock for the return operation
        LibraryService serviceDueDate = new LibraryService(bookRepo, memberRepo, loanRepo, dueDateClock);
        double fine = serviceDueDate.returnBook("ISBN-1");
        assertEquals(0.0, fine, "No fine should be charged on the due date");
    }

    @Test
    @DisplayName("returnBook: 1 day late incurs Rs.2 fine")
    void returnBook_oneDayLate_fineRs2() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.registerMember("M001", "Member", "m@test.com");
        service.issueBook("ISBN-1", "M001");

        // Clock set to 1 day after due date
        LocalDate oneDayLate = FIXED_DATE.plusDays(Loan.LOAN_PERIOD_DAYS + 1);
        Clock lateClock = Clock.fixed(
                oneDayLate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());

        LibraryService serviceLate = new LibraryService(bookRepo, memberRepo, loanRepo, lateClock);
        double fine = serviceLate.returnBook("ISBN-1");
        assertEquals(2.0, fine, "1 day late should incur Rs.2 fine");
    }

    /**
     * Parameterized test: fine calculation for various overdue durations.
     * Fine = overdueDays * Rs.2 per day.
     */
    @ParameterizedTest(name = "{0} day(s) late → fine Rs.{1}")
    @CsvSource({
            "1,  2.0",
            "5,  10.0",
            "10, 20.0",
            "30, 60.0"
    })
    @DisplayName("returnBook: parameterized fine calculation")
    void returnBook_parameterizedFine(int daysLate, double expectedFine) {
        // Fresh repos for each parameterized run
        BookRepository br = new InMemoryBookRepository();
        MemberRepository mr = new InMemoryMemberRepository();
        LoanRepository lr = new InMemoryLoanRepository();
        LibraryService svc = new LibraryService(br, mr, lr, FIXED_CLOCK);

        svc.addBook("ISBN-P", "Parameterized Book", "Author");
        svc.registerMember("M-P", "Param Member", "pm@test.com");
        svc.issueBook("ISBN-P", "M-P");

        // Return daysLate days after the due date
        LocalDate returnDate = FIXED_DATE.plusDays(Loan.LOAN_PERIOD_DAYS + daysLate);
        Clock returnClock = Clock.fixed(
                returnDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        LibraryService svcReturn = new LibraryService(br, mr, lr, returnClock);

        double fine = svcReturn.returnBook("ISBN-P");
        assertEquals(expectedFine, fine, 0.001);
    }

    @Test
    @DisplayName("returnBook: non-existent book throws BookNotFoundException")
    void returnBook_bookNotFound_throws() {
        assertThrows(BookNotFoundException.class,
                () -> service.returnBook("UNKNOWN"));
    }

    @Test
    @DisplayName("returnBook: book not on loan throws LoanNotFoundException")
    void returnBook_notOnLoan_throws() {
        service.addBook("ISBN-1", "Book A", "Author A");
        assertThrows(LoanNotFoundException.class,
                () -> service.returnBook("ISBN-1"));
    }

    // ==================== ACTIVE & OVERDUE LOANS ====================

    @Test
    @DisplayName("getActiveLoans: returns only active loans for the member")
    void getActiveLoans_returnsActive() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.addBook("ISBN-2", "Book B", "Author B");
        service.registerMember("M001", "Member", "m@test.com");

        service.issueBook("ISBN-1", "M001");
        service.issueBook("ISBN-2", "M001");
        service.returnBook("ISBN-1"); // Return one

        List<Loan> active = service.getActiveLoans("M001");
        assertEquals(1, active.size());
        assertEquals("ISBN-2", active.get(0).getBookIsbn());
    }

    @Test
    @DisplayName("getActiveLoans: unknown member throws MemberNotFoundException")
    void getActiveLoans_unknownMember_throws() {
        assertThrows(MemberNotFoundException.class,
                () -> service.getActiveLoans("UNKNOWN"));
    }

    @Test
    @DisplayName("getOverdueLoans: detects overdue loans correctly")
    void getOverdueLoans_detectsOverdue() {
        service.addBook("ISBN-1", "Book A", "Author A");
        service.registerMember("M001", "Member", "m@test.com");
        service.issueBook("ISBN-1", "M001");

        // Create a clock 20 days in the future (6 days overdue)
        LocalDate futureDate = FIXED_DATE.plusDays(20);
        Clock futureClock = Clock.fixed(
                futureDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());
        LibraryService futureSvc = new LibraryService(bookRepo, memberRepo, loanRepo, futureClock);

        List<Loan> overdue = futureSvc.getOverdueLoans("M001");
        assertEquals(1, overdue.size());
    }

    // ==================== MOCKITO-BASED TEST ====================

    @Test
    @DisplayName("Mockito: issueBook calls repository methods correctly")
    void issueBook_mockito_verifiesRepositoryInteractions() {
        // Create mock repositories
        BookRepository mockBookRepo = Mockito.mock(BookRepository.class);
        MemberRepository mockMemberRepo = Mockito.mock(MemberRepository.class);
        LoanRepository mockLoanRepo = Mockito.mock(LoanRepository.class);

        LibraryService mockService = new LibraryService(
                mockBookRepo, mockMemberRepo, mockLoanRepo, FIXED_CLOCK);

        // Stub the mocks
        Book testBook = new Book("ISBN-MOCK", "Mock Book", "Mock Author");
        Member testMember = new Member("M-MOCK", "Mock Member", "mock@test.com");

        when(mockBookRepo.findByIsbn("ISBN-MOCK")).thenReturn(Optional.of(testBook));
        when(mockMemberRepo.findById("M-MOCK")).thenReturn(Optional.of(testMember));
        when(mockLoanRepo.findActiveLoansByMemberId("M-MOCK")).thenReturn(List.of());

        // Execute
        Loan loan = mockService.issueBook("ISBN-MOCK", "M-MOCK");

        // Verify interactions
        verify(mockBookRepo).findByIsbn("ISBN-MOCK");
        verify(mockMemberRepo).findById("M-MOCK");
        verify(mockLoanRepo).findActiveLoansByMemberId("M-MOCK");
        verify(mockBookRepo).save(testBook); // Book marked unavailable and saved
        verify(mockLoanRepo).save(any(Loan.class)); // Loan saved

        // Verify loan details
        assertNotNull(loan);
        assertEquals("ISBN-MOCK", loan.getBookIsbn());
        assertEquals("M-MOCK", loan.getMemberId());
        assertFalse(testBook.isAvailable(), "Book should be marked unavailable after issue");
    }
}

