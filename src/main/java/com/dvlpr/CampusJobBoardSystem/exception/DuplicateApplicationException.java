package com.dvlpr.CampusJobBoardSystem.exception;

/**
 * Exception thrown when a student attempts to apply to a job they have already applied to.
 * Ensures one application per student per job.
 */
public class DuplicateApplicationException extends RuntimeException {
    
    /**
     * Constructs a new DuplicateApplicationException with the specified message.
     * 
     * @param message the detail message
     */
    public DuplicateApplicationException(String message) {
        super(message);
    }
}