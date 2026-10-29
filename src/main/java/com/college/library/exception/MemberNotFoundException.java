package com.college.library.exception;

/**
 * Thrown when an operation references a member that does not exist.
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class MemberNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the given message.
     * @param message description of the error
     */
    public MemberNotFoundException(String message) {
        super(message);
    }
}

