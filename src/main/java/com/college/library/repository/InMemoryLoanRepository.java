package com.college.library.repository;

import com.college.library.model.Loan;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link LoanRepository}.
 * Uses a HashMap for O(1) loan ID lookups and stream filtering
 * for member-based and book-based queries.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class InMemoryLoanRepository implements LoanRepository {

    /** Internal storage: loanId -> Loan mapping. */
    private final Map<String, Loan> loans = new HashMap<>();

    @Override
    public void save(Loan loan) {
        loans.put(loan.getLoanId(), loan);
    }

    @Override
    public Optional<Loan> findById(String loanId) {
        return Optional.ofNullable(loans.get(loanId));
    }

    @Override
    public List<Loan> findActiveLoansByMemberId(String memberId) {
        // Filter loans that belong to the member and are still active (not returned)
        return loans.values().stream()
                .filter(loan -> loan.getMemberId().equals(memberId) && loan.isActive())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Loan> findActiveLoanByBookIsbn(String bookIsbn) {
        // Find the active loan for a specific book (there should be at most one)
        return loans.values().stream()
                .filter(loan -> loan.getBookIsbn().equals(bookIsbn) && loan.isActive())
                .findFirst();
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(loans.values());
    }
}

