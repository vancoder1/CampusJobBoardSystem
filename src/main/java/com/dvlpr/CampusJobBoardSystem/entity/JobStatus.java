package com.dvlpr.CampusJobBoardSystem.entity;

/**
 * Enumeration of job posting statuses.
 * Jobs must be approved by admin before being visible to students.
 */
public enum JobStatus {
    /** Job is awaiting admin review */
    PENDING,
    /** Job has been approved and is visible to students */
    APPROVED,
    /** Job has been rejected by admin */
    REJECTED
}
