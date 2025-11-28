package com.dvlpr.CampusJobBoardSystem.repository;

import com.dvlpr.CampusJobBoardSystem.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for JobApplication entity operations.
 * Provides CRUD operations and custom query methods for job applications.
 */
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    /**
     * Find all applications for a specific job.
     * Used by employers to view applicants.
     * 
     * @param jobId the ID of the job
     * @return list of applications for the job
     */
    List<JobApplication> findByJobId(Long jobId);

    /**
     * Find all applications submitted by a specific student.
     * 
     * @param studentId the student's user ID
     * @return list of applications by the student
     */
    List<JobApplication> findByStudentId(Long studentId);

    /**
     * Find a specific application by job and student.
     * Used to check for duplicate applications.
     * 
     * @param jobId the ID of the job
     * @param studentId the student's user ID
     * @return Optional containing the application if found
     */
    Optional<JobApplication> findByJobIdAndStudentId(Long jobId, Long studentId);
}