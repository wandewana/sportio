package com.sportio.exception;

/**
 * Exception thrown when authentication is required but not provided or invalid.
 */
public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException() {
        super("Authentication required");
    }
    
    public UnauthorizedException(String message) {
        super(message);
    }
}

