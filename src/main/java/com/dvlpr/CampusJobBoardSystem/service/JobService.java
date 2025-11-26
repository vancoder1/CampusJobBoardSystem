package com.dvlpr.CampusJobBoardSystem.service;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.exception.ResourceNotFoundException;
import com.dvlpr.CampusJobBoardSystem.repository.JobRepository;
import com.dvlpr.CampusJobBoardSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Autowired
    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    // Employer: Post a job
        public void postJob(Job job, String employerEmail) {
        // Validate required fields
        if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Job title is required.");
        }
        if (job.getDescription() == null || job.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Job description is required.");
        }
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        job.setEmployer(employer);
        job.setStatus(JobStatus.PENDING); // Default per requirements
        jobRepository.save(job);
    }

    // Student: View only APPROVED jobs
    public List<Job> getAllApprovedJobs() {
        return jobRepository.findByStatus(JobStatus.APPROVED);
    }

    // Employer: View their own jobs
    public List<Job> getJobsByEmployer(String email) {
        User employer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return jobRepository.findByEmployerId(employer.getId());
    }

    // Admin: View all jobs (to approve/reject)
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    // Admin: Approve or Reject
    public void updateJobStatus(Long jobId, JobStatus status) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setStatus(status);
        jobRepository.save(job);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
    }
}