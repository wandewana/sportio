package com.sportio.exception;

/**
 * Exception thrown when password and confirm password do not match.
 */
public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("Passwords do not match");
    }
}

