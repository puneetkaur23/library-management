package com.college.library.exception;

/**
 * Thrown when an operation references a book that does not exist in the library.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class BookNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the given message.
     * @param message description of the error
     */
    public BookNotFoundException(String message) {
        super(message);
    }
}

