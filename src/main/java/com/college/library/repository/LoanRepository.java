package com.college.library.repository;

import com.college.library.model.Loan;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Loan persistence operations.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public interface LoanRepository {

    /**
     * Saves a loan record to the repository.
     * @param loan the loan to save
     */
    void save(Loan loan);

    /**
     * Finds a loan by its unique ID.
     * @param loanId the loan ID to search for
     * @return an Optional containing the loan, or empty if not found
     */
    Optional<Loan> findById(String loanId);

    /**
     * Finds all active (not yet returned) loans for a given member.
     * @param memberId the member ID
     * @return a list of active loans
     */
    List<Loan> findActiveLoansByMemberId(String memberId);

    /**
     * Finds the active loan for a specific book (by ISBN).
     * @param bookIsbn the ISBN of the book
     * @return an Optional containing the active loan, or empty if the book is not on loan
     */
    Optional<Loan> findActiveLoanByBookIsbn(String bookIsbn);

    /**
     * Returns all loans in the repository.
     * @return a list of all loans
     */
    List<Loan> findAll();
}

