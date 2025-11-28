package com.dvlpr.CampusJobBoardSystem.entity;

/**
 * Enumeration of user roles in the Campus Job Board System.
 * Determines access permissions and available features.
 */
public enum UserRole {
    /** Student role - can view and apply for jobs */
    STUDENT,
    /** Employer role - can post and manage job listings */
    EMPLOYER,
    /** Admin role - can manage users and approve/reject jobs */
    ADMIN
}
