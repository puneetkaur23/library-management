package com.college.library.exception;

/**
 * Thrown when attempting to return a book that is not currently on loan.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class LoanNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the given message.
     * @param message description of the error
     */
    public LoanNotFoundException(String message) {
        super(message);
    }
}

