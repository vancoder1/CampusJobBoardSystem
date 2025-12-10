package com.dvlpr.CampusJobBoardSystem.service;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobApplication;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.exception.DuplicateApplicationException;
import com.dvlpr.CampusJobBoardSystem.exception.ResourceNotFoundException;
import com.dvlpr.CampusJobBoardSystem.repository.JobApplicationRepository;
import com.dvlpr.CampusJobBoardSystem.repository.JobRepository;
import com.dvlpr.CampusJobBoardSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for job application operations.
 */
@Service
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationService(JobApplicationRepository applicationRepository,
                              JobRepository jobRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    /** Apply for a job (prevents duplicates). */
    public void applyForJob(Long jobId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (applicationRepository.findByJobIdAndStudentId(jobId, student.getId()).isPresent()) {
            throw new DuplicateApplicationException("You have already applied for this job");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setStudent(student);
        applicationRepository.save(application);
    }

    /** Get applications for a job (employer view). */
    public List<JobApplication> getApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    /** Get student's applications with job details. */
    public List<JobApplication> getStudentApplications(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return applicationRepository.findByStudentIdWithJob(student.getId());
    }
}