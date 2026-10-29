package com.college.library.exception;

/**
 * Thrown when a member tries to borrow a book that is not currently available.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class BookNotAvailableException extends RuntimeException {

    /**
     * Constructs a new exception with the given message.
     * @param message description of the error
     */
    public BookNotAvailableException(String message) {
        super(message);
    }
}

