package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.*;
import com.dvlpr.CampusJobBoardSystem.security.SecurityConfig;
import com.dvlpr.CampusJobBoardSystem.service.ApplicationService;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
 * Unit tests for EmployerController.
 * Tests employer-specific endpoints: job CRUD and viewing applications.
 */
@WebMvcTest(EmployerController.class)
@Import(SecurityConfig.class)
class EmployerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @MockBean
    private ApplicationService applicationService;

    private Job testJob;
    private User testEmployer;

    @BeforeEach
    void setUp() {
        testEmployer = new User();
        testEmployer.setId(1L);
        testEmployer.setEmail("employer@test.com");
        testEmployer.setFullName("Test Employer");
        testEmployer.setRole(UserRole.EMPLOYER);

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Software Developer");
        testJob.setDescription("This is a detailed job description for a software developer position");
        testJob.setLocation("Calgary");
        testJob.setSalary(new BigDecimal("50000"));
        testJob.setCategory("IT");
        testJob.setDeadline(LocalDate.now().plusDays(30));
        testJob.setStatus(JobStatus.PENDING);
        testJob.setEmployer(testEmployer);
    }

    // ==================== Dashboard Tests ====================

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testDashboard_ReturnsEmployerJobsInView() throws Exception {
        List<Job> employerJobs = Arrays.asList(testJob);
        when(jobService.getJobsByEmployer("employer@test.com")).thenReturn(employerJobs);

        mockMvc.perform(get("/employer/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/dashboard"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attribute("jobs", employerJobs));

        verify(jobService, times(1)).getJobsByEmployer("employer@test.com");
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testDashboard_NoJobs_ReturnsEmptyList() throws Exception {
        when(jobService.getJobsByEmployer("employer@test.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/employer/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/dashboard"))
                .andExpect(model().attributeExists("jobs"));
    }

    // ==================== Create Job Tests ====================

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testCreateJobForm_ReturnsCreateJobView() throws Exception {
        mockMvc.perform(get("/employer/job/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/create-job"))
                .andExpect(model().attributeExists("job"));
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testSaveJob_ValidJob_RedirectsToEmployerDashboard() throws Exception {
        doNothing().when(jobService).postJob(any(Job.class), eq("employer@test.com"));

        mockMvc.perform(post("/employer/job/save")
                        .with(csrf())
                        .param("title", "Software Developer")
                        .param("description", "This is a detailed job description for a software developer position")
                        .param("location", "Calgary")
                        .param("salary", "50000")
                        .param("category", "IT")
                        .param("deadline", LocalDate.now().plusDays(30).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employer/dashboard"))
                .andExpect(flash().attributeExists("success"));

        verify(jobService, times(1)).postJob(any(Job.class), eq("employer@test.com"));
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testSaveJob_BlankTitle_ReturnsCreateJobViewWithError() throws Exception {
        mockMvc.perform(post("/employer/job/save")
                        .with(csrf())
                        .param("title", "")
                        .param("description", "This is a detailed job description")
                        .param("location", "Calgary"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/create-job"))
                .andExpect(model().hasErrors());

        verify(jobService, never()).postJob(any(Job.class), anyString());
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testSaveJob_BlankDescription_ReturnsCreateJobViewWithError() throws Exception {
        mockMvc.perform(post("/employer/job/save")
                        .with(csrf())
                        .param("title", "Software Developer")
                        .param("description", "")
                        .param("location", "Calgary"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/create-job"))
                .andExpect(model().hasErrors());

        verify(jobService, never()).postJob(any(Job.class), anyString());
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testSaveJob_TitleTooShort_ReturnsCreateJobViewWithError() throws Exception {
        mockMvc.perform(post("/employer/job/save")
                        .with(csrf())
                        .param("title", "AB")
                        .param("description", "This is a detailed job description")
                        .param("location", "Calgary"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/create-job"))
                .andExpect(model().hasErrors());

        verify(jobService, never()).postJob(any(Job.class), anyString());
    }

    // ==================== Edit Job Tests ====================

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testEditJobForm_OwnJob_ReturnsEditJobView() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(testJob);

        mockMvc.perform(get("/employer/job/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/edit-job"))
                .andExpect(model().attributeExists("job"))
                .andExpect(model().attribute("job", testJob));
    }

    @Test
    @WithMockUser(username = "other@test.com", roles = "EMPLOYER")
    void testEditJobForm_OtherEmployerJob_RedirectsWithError() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(testJob);

        mockMvc.perform(get("/employer/job/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employer/dashboard"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testUpdateJob_ValidJob_RedirectsToEmployerDashboard() throws Exception {
        doNothing().when(jobService).updateJob(eq(1L), any(Job.class), eq("employer@test.com"));

        mockMvc.perform(post("/employer/job/1/update")
                        .with(csrf())
                        .param("title", "Updated Developer")
                        .param("description", "This is an updated detailed job description for the position")
                        .param("location", "Edmonton")
                        .param("salary", "60000")
                        .param("category", "IT")
                        .param("deadline", LocalDate.now().plusDays(60).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employer/dashboard"))
                .andExpect(flash().attributeExists("success"));

        verify(jobService, times(1)).updateJob(eq(1L), any(Job.class), eq("employer@test.com"));
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testUpdateJob_InvalidJob_ReturnsEditJobViewWithError() throws Exception {
        mockMvc.perform(post("/employer/job/1/update")
                        .with(csrf())
                        .param("title", "")
                        .param("description", "Updated description"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/edit-job"))
                .andExpect(model().hasErrors());

        verify(jobService, never()).updateJob(anyLong(), any(Job.class), anyString());
    }

    // ==================== Delete Job Tests ====================

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testDeleteJob_Success_RedirectsToEmployerDashboard() throws Exception {
        doNothing().when(jobService).deleteJob(eq(1L), eq("employer@test.com"));

        mockMvc.perform(post("/employer/job/1/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employer/dashboard"))
                .andExpect(flash().attributeExists("success"));

        verify(jobService, times(1)).deleteJob(1L, "employer@test.com");
    }

    // ==================== View Applications Tests ====================

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void testViewApplications_OwnJob_ReturnsApplicationsView() throws Exception {
        User student = new User();
        student.setId(2L);
        student.setEmail("student@test.com");
        student.setFullName("Test Student");

        JobApplication application = new JobApplication();
        application.setId(1L);
        application.setJob(testJob);
        application.setStudent(student);
        application.setStatus(ApplicationStatus.SUBMITTED);

        List<JobApplication> applications = Arrays.asList(application);

        when(jobService.getJobById(1L)).thenReturn(testJob);
        when(applicationService.getApplicationsForJob(1L)).thenReturn(applications);

        mockMvc.perform(get("/employer/job/1/applications"))
                .andExpect(status().isOk())
                .andExpect(view().name("employer/view-applications"))
                .andExpect(model().attributeExists("job"))
                .andExpect(model().attributeExists("applications"));

        verify(applicationService, times(1)).getApplicationsForJob(1L);
    }

    @Test
    @WithMockUser(username = "other@test.com", roles = "EMPLOYER")
    void testViewApplications_OtherEmployerJob_RedirectsWithError() throws Exception {
        when(jobService.getJobById(1L)).thenReturn(testJob);

        mockMvc.perform(get("/employer/job/1/applications"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employer/dashboard"))
                .andExpect(flash().attributeExists("error"));

        verify(applicationService, never()).getApplicationsForJob(anyLong());
    }

    // ==================== Authorization Tests ====================

    @Test
    void testDashboard_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/employer/dashboard"))
                .andExpect(status().is3xxRedirection());
    }
}
