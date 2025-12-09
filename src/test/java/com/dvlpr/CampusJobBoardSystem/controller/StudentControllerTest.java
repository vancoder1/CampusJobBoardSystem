package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.*;
import com.dvlpr.CampusJobBoardSystem.exception.DuplicateApplicationException;
import com.dvlpr.CampusJobBoardSystem.security.SecurityConfig;
import com.dvlpr.CampusJobBoardSystem.service.ApplicationService;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for StudentController.
 * Tests student-specific endpoints: job browsing, job details, and applications.
 */
@WebMvcTest(StudentController.class)
@Import({SecurityConfig.class, StudentControllerTest.TestConfig.class})
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    private Job testJob;
    private User testEmployer;

    @BeforeEach
    void setUp() {
        // Reset mocks to clear invocation counts between tests
        reset(jobService, applicationService);

        testEmployer = new User();
        testEmployer.setId(1L);
        testEmployer.setEmail("employer@test.com");
        testEmployer.setFullName("Test Employer");
        testEmployer.setRole(UserRole.EMPLOYER);

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Software Developer");
        testJob.setDescription("Java developer position");
        testJob.setLocation("Calgary");
        testJob.setSalary(new BigDecimal("50000"));
        testJob.setCategory("IT");
        testJob.setDeadline(LocalDate.now().plusDays(30));
        testJob.setStatus(JobStatus.APPROVED);
        testJob.setEmployer(testEmployer);
    }

    // ==================== Dashboard Tests ====================

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testListJobs_ReturnsApprovedJobsInDashboard() throws Exception {
        List<Job> approvedJobs = Arrays.asList(testJob);
        when(jobService.getAllApprovedJobs()).thenReturn(approvedJobs);

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attribute("jobs", approvedJobs));

        verify(jobService, times(1)).getAllApprovedJobs();
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testListJobs_EmptyList_ReturnsEmptyJobsInDashboard() throws Exception {
        when(jobService.getAllApprovedJobs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/dashboard"))
                .andExpect(model().attributeExists("jobs"));
    }

    // ==================== Job Details Tests ====================

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testViewJobDetails_ApprovedJob_ReturnsJobDetailsView() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(testJob);

        mockMvc.perform(get("/student/job/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/job-details"))
                .andExpect(model().attributeExists("job"))
                .andExpect(model().attribute("job", testJob));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testViewJobDetails_PendingJob_RedirectsToDashboard() throws Exception {
        testJob.setStatus(JobStatus.PENDING);
        when(jobService.getJobById(1L)).thenReturn(testJob);

        mockMvc.perform(get("/student/job/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/student/dashboard*"));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testViewJobDetails_RejectedJob_RedirectsToDashboard() throws Exception {
        testJob.setStatus(JobStatus.REJECTED);
        when(jobService.getJobById(1L)).thenReturn(testJob);

        mockMvc.perform(get("/student/job/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/student/dashboard*"));
    }

    // ==================== My Applications Tests ====================

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testMyApplications_ReturnsApplicationsView() throws Exception {
        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setJob(testJob);
        application.setStatus(ApplicationStatus.SUBMITTED);

        List<JobApplication> applications = Arrays.asList(application);
        when(applicationService.getStudentApplications("student@test.com")).thenReturn(applications);

        mockMvc.perform(get("/student/my-applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/my-applications"))
                .andExpect(model().attributeExists("applications"));

        verify(applicationService, times(1)).getStudentApplications("student@test.com");
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testMyApplications_NoApplications_ReturnsEmptyList() throws Exception {
        when(applicationService.getStudentApplications("student@test.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/student/my-applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("student/my-applications"))
                .andExpect(model().attributeExists("applications"));
    }

    // ==================== Apply for Job Tests ====================

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testApplyJob_Success_RedirectsWithSuccessMessage() throws Exception {
        doNothing().when(applicationService).applyForJob(eq(1L), eq("student@test.com"));

        mockMvc.perform(post("/student/job/1/apply")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(applicationService, times(1)).applyForJob(1L, "student@test.com");
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testApplyJob_DuplicateApplication_RedirectsWithErrorMessage() throws Exception {
        doThrow(new DuplicateApplicationException("You have already applied for this job"))
                .when(applicationService).applyForJob(eq(1L), eq("student@test.com"));

        mockMvc.perform(post("/student/job/1/apply")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"))
                .andExpect(flash().attributeExists("errorMessage"));

        verify(applicationService, times(1)).applyForJob(1L, "student@test.com");
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testApplyJob_GenericException_RedirectsWithErrorMessage() throws Exception {
        doThrow(new RuntimeException("Unexpected error"))
                .when(applicationService).applyForJob(eq(1L), eq("student@test.com"));

        mockMvc.perform(post("/student/job/1/apply")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    // ==================== Authorization Tests ====================

    @Test
    void testDashboard_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JobService jobService() {
            return mock(JobService.class);
        }

        @Bean
        public ApplicationService applicationService() {
            return mock(ApplicationService.class);
        }
    }
}
