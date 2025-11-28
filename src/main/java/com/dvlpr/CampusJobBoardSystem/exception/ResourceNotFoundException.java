package com.dvlpr.CampusJobBoardSystem.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Used for entity lookups that fail (e.g., job, user, application not found).
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new ResourceNotFoundException with the specified message.
     * 
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}