package com.college.library.repository;

import com.college.library.model.Book;
import com.college.library.model.Loan;
import com.college.library.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the in-memory repository implementations.
 */
class RepositoryTest {

    private InMemoryBookRepository bookRepo;
    private InMemoryMemberRepository memberRepo;
    private InMemoryLoanRepository loanRepo;

    @BeforeEach
    void setUp() {
        bookRepo = new InMemoryBookRepository();
        memberRepo = new InMemoryMemberRepository();
        loanRepo = new InMemoryLoanRepository();
    }

    // ==================== BOOK REPOSITORY TESTS ====================

    @Test
    @DisplayName("BookRepo: save and findByIsbn returns the book")
    void bookRepo_saveAndFind() {
        Book book = new Book("ISBN-1", "Test Title", "Test Author");
        bookRepo.save(book);

        Optional<Book> found = bookRepo.findByIsbn("ISBN-1");
        assertTrue(found.isPresent());
        assertEquals("Test Title", found.get().getTitle());
    }

    @Test
    @DisplayName("BookRepo: findByIsbn returns empty for unknown ISBN")
    void bookRepo_findByIsbn_unknownReturnsEmpty() {
        assertTrue(bookRepo.findByIsbn("UNKNOWN").isEmpty());
    }

    @Test
    @DisplayName("BookRepo: deleteByIsbn removes the book")
    void bookRepo_deleteByIsbn() {
        bookRepo.save(new Book("ISBN-1", "Title", "Author"));
        assertTrue(bookRepo.deleteByIsbn("ISBN-1"));
        assertTrue(bookRepo.findByIsbn("ISBN-1").isEmpty());
    }

    @Test
    @DisplayName("BookRepo: findByTitleContaining performs case-insensitive search")
    void bookRepo_findByTitle_caseInsensitive() {
        bookRepo.save(new Book("ISBN-1", "Clean Code", "Robert Martin"));
        bookRepo.save(new Book("ISBN-2", "Dirty Hands", "Jean Sartre"));

        List<Book> results = bookRepo.findByTitleContaining("clean");
        assertEquals(1, results.size());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    @Test
    @DisplayName("BookRepo: findByAuthorContaining performs case-insensitive search")
    void bookRepo_findByAuthor_caseInsensitive() {
        bookRepo.save(new Book("ISBN-1", "Book A", "Robert Martin"));
        bookRepo.save(new Book("ISBN-2", "Book B", "Martin Fowler"));

        List<Book> results = bookRepo.findByAuthorContaining("martin");
        assertEquals(2, results.size());
    }

    // ==================== LOAN REPOSITORY TESTS ====================

    @Test
    @DisplayName("LoanRepo: findActiveLoansByMemberId returns only active loans")
    void loanRepo_findActiveLoans_returnsOnlyActive() {
        Loan activeLoan = new Loan("L1", "ISBN-1", "M001", LocalDate.now());
        Loan returnedLoan = new Loan("L2", "ISBN-2", "M001", LocalDate.now());
        returnedLoan.setReturnDate(LocalDate.now());

        loanRepo.save(activeLoan);
        loanRepo.save(returnedLoan);

        List<Loan> active = loanRepo.findActiveLoansByMemberId("M001");
        assertEquals(1, active.size());
        assertEquals("L1", active.get(0).getLoanId());
    }

    @Test
    @DisplayName("LoanRepo: findActiveLoanByBookIsbn returns active loan for book")
    void loanRepo_findActiveLoanByBook() {
        Loan loan = new Loan("L1", "ISBN-1", "M001", LocalDate.now());
        loanRepo.save(loan);

        assertTrue(loanRepo.findActiveLoanByBookIsbn("ISBN-1").isPresent());
        assertTrue(loanRepo.findActiveLoanByBookIsbn("ISBN-UNKNOWN").isEmpty());
    }
}

