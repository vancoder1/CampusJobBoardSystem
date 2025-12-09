package com.dvlpr.CampusJobBoardSystem.repository;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Search approved jobs by keyword in title, description, location, or category.
     *
     * @param keyword the search keyword
     * @param status the job status to filter by (should be APPROVED for students)
     * @return list of matching jobs
     */
    @Query("SELECT j FROM Job j WHERE j.status = :status AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Job> searchByKeywordAndStatus(@Param("keyword") String keyword, @Param("status") JobStatus status);

    /**
     * Find approved jobs by category.
     *
     * @param category the job category
     * @param status the job status to filter by
     * @return list of jobs in the specified category
     */
    List<Job> findByCategoryIgnoreCaseAndStatus(String category, JobStatus status);

    /**
     * Find approved jobs by location.
     *
     * @param location the job location
     * @param status the job status to filter by
     * @return list of jobs in the specified location
     */
    List<Job> findByLocationIgnoreCaseContainingAndStatus(String location, JobStatus status);

    /**
     * Get distinct categories from approved jobs.
     *
     * @param status the job status to filter by
     * @return list of distinct categories
     */
    @Query("SELECT DISTINCT j.category FROM Job j WHERE j.status = :status AND j.category IS NOT NULL")
    List<String> findDistinctCategoriesByStatus(@Param("status") JobStatus status);

    /**
     * Get distinct locations from approved jobs.
     *
     * @param status the job status to filter by
     * @return list of distinct locations
     */
    @Query("SELECT DISTINCT j.location FROM Job j WHERE j.status = :status AND j.location IS NOT NULL")
    List<String> findDistinctLocationsByStatus(@Param("status") JobStatus status);
}