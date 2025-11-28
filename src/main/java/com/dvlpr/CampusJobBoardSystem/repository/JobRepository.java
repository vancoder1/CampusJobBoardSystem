package com.dvlpr.CampusJobBoardSystem.repository;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for Job entity operations.
 * Provides CRUD operations and custom query methods for job postings.
 */
public interface JobRepository extends JpaRepository<Job, Long> {
    
    /**
     * Find all jobs with a specific status.
     * Used to retrieve approved jobs for students.
     * 
     * @param status the job status to filter by
     * @return list of jobs with the specified status
     */
    List<Job> findByStatus(JobStatus status);

    /**
     * Find all jobs posted by a specific employer.
     * 
     * @param employerId the employer's user ID
     * @return list of jobs posted by the employer
     */
    List<Job> findByEmployerId(Long employerId);
}