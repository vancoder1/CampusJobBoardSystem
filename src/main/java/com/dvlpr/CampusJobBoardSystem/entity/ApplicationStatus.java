package com.dvlpr.CampusJobBoardSystem.entity;

/**
 * Enumeration of job application statuses.
 * Tracks the state of a student's application to a job.
 */
public enum ApplicationStatus {
    /** Application has been submitted and is pending review */
    SUBMITTED,
    /** Application has been accepted by the employer */
    ACCEPTED,
    /** Application has been rejected by the employer */
    REJECTED
}