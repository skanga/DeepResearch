package com.skanga.exception;

/**
 * Base exception for all search‑related errors.
 */
public class SearchException extends RuntimeException {
    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
