package com.college.library.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Book}, {@link Member}, and {@link Loan} model classes.
 * Tests constructors, validation, equality, and business-logic helper methods.
 */
class ModelTest {

    // ==================== BOOK TESTS ====================

    @Test
    @DisplayName("Book: valid construction sets all fields correctly")
    void bookConstructor_validInput_setsFieldsCorrectly() {
        Book book = new Book("978-0-13-468599-1", "The Pragmatic Programmer", "David Thomas");

        assertEquals("978-0-13-468599-1", book.getIsbn());
        assertEquals("The Pragmatic Programmer", book.getTitle());
        assertEquals("David Thomas", book.getAuthor());
        assertTrue(book.isAvailable(), "New book should be available by default");
    }

    @ParameterizedTest(name = "Book: blank ISBN \"{0}\" throws IllegalArgumentException")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t"})
    @DisplayName("Book: blank ISBN throws IllegalArgumentException")
    void bookConstructor_blankIsbn_throwsException(String isbn) {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(isbn, "Title", "Author"));
    }

    @Test
    @DisplayName("Book: blank title throws IllegalArgumentException")
    void bookConstructor_blankTitle_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("ISBN-1", "", "Author"));
    }

    @Test
    @DisplayName("Book: blank author throws IllegalArgumentException")
    void bookConstructor_blankAuthor_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book("ISBN-1", "Title", "  "));
    }

    @Test
    @DisplayName("Book: books with same ISBN are equal")
    void bookEquals_sameIsbn_areEqual() {
        Book book1 = new Book("ISBN-1", "Title A", "Author A");
        Book book2 = new Book("ISBN-1", "Title B", "Author B");
        assertEquals(book1, book2, "Books with the same ISBN should be equal");
        assertEquals(book1.hashCode(), book2.hashCode());
    }

    @Test
    @DisplayName("Book: books with different ISBNs are not equal")
    void bookEquals_differentIsbn_areNotEqual() {
        Book book1 = new Book("ISBN-1", "Title", "Author");
        Book book2 = new Book("ISBN-2", "Title", "Author");
        assertNotEquals(book1, book2);
    }

    @Test
    @DisplayName("Book: setAvailable toggles availability")
    void bookSetAvailable_togglesCorrectly() {
        Book book = new Book("ISBN-1", "Title", "Author");
        assertTrue(book.isAvailable());

        book.setAvailable(false);
        assertFalse(book.isAvailable());

        book.setAvailable(true);
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("Book: toString returns formatted string")
    void bookToString_returnsFormattedString() {
        Book book = new Book("ISBN-1", "Clean Code", "Robert Martin");
        String str = book.toString();
        assertTrue(str.contains("ISBN-1"));
        assertTrue(str.contains("Clean Code"));
        assertTrue(str.contains("Robert Martin"));
    }

    // ==================== MEMBER TESTS ====================

    @Test
    @DisplayName("Member: valid construction sets all fields correctly")
    void memberConstructor_validInput_setsFieldsCorrectly() {
        Member member = new Member("M001", "Aarav Sharma", "aarav@college.edu");

        assertEquals("M001", member.getMemberId());
        assertEquals("Aarav Sharma", member.getName());
        assertEquals("aarav@college.edu", member.getEmail());
    }

    @Test
    @DisplayName("Member: blank ID throws IllegalArgumentException")
    void memberConstructor_blankId_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Member("", "Name", "email@test.com"));
    }

    @Test
    @DisplayName("Member: members with same ID are equal")
    void memberEquals_sameId_areEqual() {
        Member m1 = new Member("M001", "Name A", "a@test.com");
        Member m2 = new Member("M001", "Name B", "b@test.com");
        assertEquals(m1, m2);
    }

    // ==================== LOAN TESTS ====================

    @Test
    @DisplayName("Loan: construction calculates due date correctly")
    void loanConstructor_calculatesDueDate() {
        LocalDate issueDate = LocalDate.of(2026, 10, 1);
        Loan loan = new Loan("L1", "ISBN-1", "M001", issueDate);

        assertEquals(issueDate.plusDays(Loan.LOAN_PERIOD_DAYS), loan.getDueDate());
        assertTrue(loan.isActive(), "New loan should be active");
        assertNull(loan.getReturnDate(), "New loan should have no return date");
        assertEquals(0.0, loan.getFine(), "New loan should have zero fine");
    }

    @Test
    @DisplayName("Loan: isOverdue returns false on due date")
    void loanIsOverdue_onDueDate_returnsFalse() {
        LocalDate issueDate = LocalDate.of(2026, 10, 1);
        Loan loan = new Loan("L1", "ISBN-1", "M001", issueDate);
        // Due date = Oct 15
        assertFalse(loan.isOverdue(loan.getDueDate()), "Should NOT be overdue on the due date");
    }

    @Test
    @DisplayName("Loan: isOverdue returns true one day after due date")
    void loanIsOverdue_oneDayAfterDue_returnsTrue() {
        LocalDate issueDate = LocalDate.of(2026, 10, 1);
        Loan loan = new Loan("L1", "ISBN-1", "M001", issueDate);
        // One day after due date
        assertTrue(loan.isOverdue(loan.getDueDate().plusDays(1)));
    }

    @Test
    @DisplayName("Loan: isOverdue returns false after return")
    void loanIsOverdue_afterReturn_returnsFalse() {
        LocalDate issueDate = LocalDate.of(2026, 10, 1);
        Loan loan = new Loan("L1", "ISBN-1", "M001", issueDate);
        loan.setReturnDate(loan.getDueDate().plusDays(5));
        // After return, loan is no longer active, so not overdue
        assertFalse(loan.isOverdue(loan.getDueDate().plusDays(10)));
    }

    @Test
    @DisplayName("Loan: constants have expected values")
    void loanConstants_haveExpectedValues() {
        assertEquals(14, Loan.LOAN_PERIOD_DAYS);
        assertEquals(2.0, Loan.FINE_PER_DAY);
        assertEquals(3, Loan.MAX_LOANS_PER_MEMBER);
    }
}

