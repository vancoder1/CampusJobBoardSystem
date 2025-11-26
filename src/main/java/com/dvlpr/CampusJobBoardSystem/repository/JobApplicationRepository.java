package com.dvlpr.CampusJobBoardSystem.repository;

import com.dvlpr.CampusJobBoardSystem.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    // For Employers (View applicants for a specific job)
    List<JobApplication> findByJobId(Long jobId);

    // For Students (View their own applications)
    List<JobApplication> findByStudentId(Long studentId);

    // To check duplicate applications
    Optional<JobApplication> findByJobIdAndStudentId(Long jobId, Long studentId);
}