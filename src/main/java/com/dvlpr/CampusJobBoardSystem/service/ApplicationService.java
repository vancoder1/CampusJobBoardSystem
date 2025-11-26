package com.dvlpr.CampusJobBoardSystem.service;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobApplication;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.exception.DuplicateApplicationException;
import com.dvlpr.CampusJobBoardSystem.exception.ResourceNotFoundException;
import com.dvlpr.CampusJobBoardSystem.repository.JobApplicationRepository;
import com.dvlpr.CampusJobBoardSystem.repository.JobRepository;
import com.dvlpr.CampusJobBoardSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationService(JobApplicationRepository applicationRepository,
                              JobRepository jobRepository,
                              UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public void applyForJob(Long jobId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        // Check for duplicate application (Rubric Requirement)
        if (applicationRepository.findByJobIdAndStudentId(jobId, student.getId()).isPresent()) {
            throw new DuplicateApplicationException("You have already applied for this job.");
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setStudent(student);
        // Default status is SUBMITTED

        applicationRepository.save(application);
    }

    // Employer: See who applied to their job
    public List<JobApplication> getApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // Student: See their own applications
    public List<JobApplication> getStudentApplications(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return applicationRepository.findByStudentId(student.getId());
    }
}