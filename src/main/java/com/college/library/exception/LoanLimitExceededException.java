package com.college.library.exception;

/**
 * Thrown when a member has reached the maximum number of active loans
 * and tries to borrow another book.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class LoanLimitExceededException extends RuntimeException {

    /**
     * Constructs a new exception with the given message.
     * @param message description of the error
     */
    public LoanLimitExceededException(String message) {
        super(message);
    }
}

