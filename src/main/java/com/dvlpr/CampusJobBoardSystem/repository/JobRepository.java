package com.dvlpr.CampusJobBoardSystem.repository;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    // For Students (View only approved jobs)
    List<Job> findByStatus(JobStatus status);

    // For Employers (View their own jobs)
    List<Job> findByEmployerId(Long employerId);
}