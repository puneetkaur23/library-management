package com.college.library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a book loan (borrowing record) in the library system.
 * <p>
 * A loan tracks which member borrowed which book, the issue date,
 * the due date, the return date (if returned), and any fine incurred.
 * </p>
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class Loan {

    /** Default loan period in days. */
    public static final int LOAN_PERIOD_DAYS = 14;

    /** Fine per overdue day in Rupees. */
    public static final double FINE_PER_DAY = 2.0;

    /** Maximum number of active loans allowed per member. */
    public static final int MAX_LOANS_PER_MEMBER = 3;

    /** The unique loan identifier. */
    private final String loanId;

    /** The ISBN of the borrowed book. */
    private final String bookIsbn;

    /** The ID of the borrowing member. */
    private final String memberId;

    /** The date the book was issued. */
    private final LocalDate issueDate;

    /** The date the book is due for return. */
    private final LocalDate dueDate;

    /** The date the book was actually returned (null if still on loan). */
    private LocalDate returnDate;

    /** The fine amount in Rupees (0.0 if returned on time or not yet returned). */
    private double fine;

    /**
     * Constructs a new Loan.
     *
     * @param loanId    the unique loan ID
     * @param bookIsbn  the ISBN of the borrowed book
     * @param memberId  the ID of the borrowing member
     * @param issueDate the date the book was issued
     */
    public Loan(String loanId, String bookIsbn, String memberId, LocalDate issueDate) {
        this.loanId = loanId;
        this.bookIsbn = bookIsbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        // Due date is calculated as issue date + loan period
        this.dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
        this.returnDate = null; // Not yet returned
        this.fine = 0.0;
    }

    /** @return the unique loan ID */
    public String getLoanId() {
        return loanId;
    }

    /** @return the ISBN of the borrowed book */
    public String getBookIsbn() {
        return bookIsbn;
    }

    /** @return the member ID of the borrower */
    public String getMemberId() {
        return memberId;
    }

    /** @return the issue date */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /** @return the due date */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /** @return the return date, or {@code null} if not yet returned */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date of this loan.
     * @param returnDate the date the book was returned
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /** @return the fine amount in Rupees */
    public double getFine() {
        return fine;
    }

    /**
     * Sets the fine for this loan.
     * @param fine the fine amount in Rupees
     */
    public void setFine(double fine) {
        this.fine = fine;
    }

    /**
     * Checks whether this loan is currently active (book not yet returned).
     * @return {@code true} if the book has not been returned
     */
    public boolean isActive() {
        return returnDate == null;
    }

    /**
     * Checks whether this loan is overdue as of the given date.
     * A loan is overdue if it is active and the current date is after the due date.
     *
     * @param currentDate the date to check against
     * @return {@code true} if the loan is overdue
     */
    public boolean isOverdue(LocalDate currentDate) {
        return isActive() && currentDate.isAfter(dueDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(loanId, loan.loanId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loanId);
    }

    @Override
    public String toString() {
        return String.format(
                "Loan{id='%s', book='%s', member='%s', issued=%s, due=%s, returned=%s, fine=Rs.%.2f}",
                loanId, bookIsbn, memberId, issueDate, dueDate,
                returnDate != null ? returnDate.toString() : "N/A", fine);
    }
}

