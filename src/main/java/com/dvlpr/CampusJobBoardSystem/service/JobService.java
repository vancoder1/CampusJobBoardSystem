package com.dvlpr.CampusJobBoardSystem.service;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.exception.ResourceNotFoundException;
import com.dvlpr.CampusJobBoardSystem.repository.JobRepository;
import com.dvlpr.CampusJobBoardSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for job-related operations.
 */
@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    /** Post a new job (status: PENDING until admin approves). */
    public void postJob(Job job, String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        job.setEmployer(employer);
        job.setStatus(JobStatus.PENDING);
        jobRepository.save(job);
    }

    /** Update an existing job (resets to PENDING). */
    public void updateJob(Long jobId, Job updatedJob, String employerEmail) {
        Job job = getJobById(jobId);
        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new IllegalArgumentException("Unauthorized to update this job");
        }
        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setLocation(updatedJob.getLocation());
        job.setSalary(updatedJob.getSalary());
        job.setCategory(updatedJob.getCategory());
        job.setDeadline(updatedJob.getDeadline());
        job.setStatus(JobStatus.PENDING);
        jobRepository.save(job);
    }

    /** Delete a job. */
    public void deleteJob(Long jobId, String employerEmail) {
        Job job = getJobById(jobId);
        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new IllegalArgumentException("Unauthorized to delete this job");
        }
        jobRepository.delete(job);
    }

    /** Get all approved jobs (for students). */
    public List<Job> getAllApprovedJobs() {
        return jobRepository.findByStatus(JobStatus.APPROVED);
    }

    /** Get jobs by employer. */
    public List<Job> getJobsByEmployer(String email) {
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return jobRepository.findByEmployerId(employer.getId());
    }

    /** Get all jobs (for admin). */
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    /** Update job status (admin approve/reject). */
    public void updateJobStatus(Long jobId, JobStatus status) {
        Job job = getJobById(jobId);
        job.setStatus(status);
        jobRepository.save(job);
    }

    /** Get job by ID. */
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }

    /**
     * Search approved jobs by keyword.
     * Searches in title, description, location, and category.
     *
     * @param keyword the search keyword
     * @return list of matching approved jobs
     */
    public List<Job> searchApprovedJobs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllApprovedJobs();
        }
        return jobRepository.searchByKeywordAndStatus(keyword.trim(), JobStatus.APPROVED);
    }

    /**
     * Get approved jobs by category.
     *
     * @param category the job category
     * @return list of approved jobs in the specified category
     */
    public List<Job> getApprovedJobsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return getAllApprovedJobs();
        }
        return jobRepository.findByCategoryIgnoreCaseAndStatus(category.trim(), JobStatus.APPROVED);
    }

    /**
     * Get approved jobs by location.
     *
     * @param location the job location
     * @return list of approved jobs in the specified location
     */
    public List<Job> getApprovedJobsByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return getAllApprovedJobs();
        }
        return jobRepository.findByLocationIgnoreCaseContainingAndStatus(location.trim(), JobStatus.APPROVED);
    }

    /**
     * Get all distinct categories from approved jobs.
     *
     * @return list of distinct categories
     */
    public List<String> getAvailableCategories() {
        return jobRepository.findDistinctCategoriesByStatus(JobStatus.APPROVED);
    }

    /**
     * Get all distinct locations from approved jobs.
     *
     * @return list of distinct locations
     */
    public List<String> getAvailableLocations() {
        return jobRepository.findDistinctLocationsByStatus(JobStatus.APPROVED);
    }
}