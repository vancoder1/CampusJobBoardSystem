package com.dvlpr.CampusJobBoardSystem.service;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.repository.JobRepository;
import com.dvlpr.CampusJobBoardSystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void testPostJob_SetsStatusToPending() {
        // Arrange
        String email = "employer@company.com";
        User employer = new User();
        employer.setId(1L);
        employer.setEmail(email);

        Job job = new Job();
        job.setTitle("Java Developer");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(employer));

        // Act
        jobService.postJob(job, email);

        // Assert
        assertEquals(JobStatus.PENDING, job.getStatus()); // Crucial logic check
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    void testUpdateJobStatus_AdminApproves() {
        // Arrange
        Long jobId = 10L;
        Job job = new Job();
        job.setId(jobId);
        job.setStatus(JobStatus.PENDING);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        // Act
        jobService.updateJobStatus(jobId, JobStatus.APPROVED);

        // Assert
        assertEquals(JobStatus.APPROVED, job.getStatus());
        verify(jobRepository, times(1)).save(job);
    }
}