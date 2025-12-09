package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.*;
import com.dvlpr.CampusJobBoardSystem.security.SecurityConfig;
import com.dvlpr.CampusJobBoardSystem.service.AdminService;
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

import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AdminController.
 * Tests admin-specific endpoints: job approval/rejection and user management.
 */
@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, AdminControllerTest.TestConfig.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobService jobService;

    @Autowired
    private AdminService adminService;

    private Job testJob;
    private User testEmployer;
    private User testStudent;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        // Reset mocks to clear invocation counts between tests
        reset(jobService, adminService);

        testEmployer = new User();
        testEmployer.setId(1L);
        testEmployer.setEmail("employer@test.com");
        testEmployer.setFullName("Test Employer");
        testEmployer.setRole(UserRole.EMPLOYER);
        testEmployer.setStatus(UserStatus.ACTIVE);

        testStudent = new User();
        testStudent.setId(2L);
        testStudent.setEmail("student@test.com");
        testStudent.setFullName("Test Student");
        testStudent.setRole(UserRole.STUDENT);
        testStudent.setStatus(UserStatus.ACTIVE);

        testAdmin = new User();
        testAdmin.setId(3L);
        testAdmin.setEmail("admin@test.com");
        testAdmin.setFullName("Test Admin");
        testAdmin.setRole(UserRole.ADMIN);
        testAdmin.setStatus(UserStatus.ACTIVE);

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Software Developer");
        testJob.setDescription("Java developer position");
        testJob.setLocation("Calgary");
        testJob.setSalary(new BigDecimal("50000"));
        testJob.setCategory("IT");
        testJob.setDeadline(LocalDate.now().plusDays(30));
        testJob.setStatus(JobStatus.PENDING);
        testJob.setEmployer(testEmployer);
    }

    // ==================== Dashboard Tests ====================

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testDashboard_ReturnsAllJobsInView() throws Exception {
        List<Job> allJobs = Arrays.asList(testJob);
        when(jobService.getAllJobs()).thenReturn(allJobs);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("jobs"))
                .andExpect(model().attribute("jobs", allJobs));

        verify(jobService, times(1)).getAllJobs();
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testDashboard_NoJobs_ReturnsEmptyList() throws Exception {
        when(jobService.getAllJobs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("jobs"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testDashboard_MultipleJobsWithDifferentStatuses_ReturnsAllJobs() throws Exception {
        Job approvedJob = new Job();
        approvedJob.setId(2L);
        approvedJob.setTitle("Approved Job");
        approvedJob.setStatus(JobStatus.APPROVED);
        approvedJob.setEmployer(testEmployer);

        Job rejectedJob = new Job();
        rejectedJob.setId(3L);
        rejectedJob.setTitle("Rejected Job");
        rejectedJob.setStatus(JobStatus.REJECTED);
        rejectedJob.setEmployer(testEmployer);

        List<Job> allJobs = Arrays.asList(testJob, approvedJob, rejectedJob);
        when(jobService.getAllJobs()).thenReturn(allJobs);

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("jobs", allJobs));
    }

    // ==================== Approve Job Tests ====================

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testApproveJob_Success_RedirectsToAdminDashboard() throws Exception {
        doNothing().when(jobService).updateJobStatus(eq(1L), eq(JobStatus.APPROVED));

        mockMvc.perform(post("/admin/job/1/approve")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?approved"));

        verify(jobService, times(1)).updateJobStatus(1L, JobStatus.APPROVED);
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testApproveJob_MultipleJobs_AllApproved() throws Exception {
        doNothing().when(jobService).updateJobStatus(anyLong(), eq(JobStatus.APPROVED));

        // Approve first job
        mockMvc.perform(post("/admin/job/1/approve")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Approve second job
        mockMvc.perform(post("/admin/job/2/approve")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(jobService, times(1)).updateJobStatus(1L, JobStatus.APPROVED);
        verify(jobService, times(1)).updateJobStatus(2L, JobStatus.APPROVED);
    }

    // ==================== Reject Job Tests ====================

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testRejectJob_Success_RedirectsToAdminDashboard() throws Exception {
        doNothing().when(jobService).updateJobStatus(eq(1L), eq(JobStatus.REJECTED));

        mockMvc.perform(post("/admin/job/1/reject")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard?rejected"));

        verify(jobService, times(1)).updateJobStatus(1L, JobStatus.REJECTED);
    }

    // ==================== User Management Tests ====================

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testManageUsers_ReturnsAllUsersInView() throws Exception {
        List<User> allUsers = Arrays.asList(testEmployer, testStudent, testAdmin);
        when(adminService.getAllUsers()).thenReturn(allUsers);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", allUsers));

        verify(adminService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testManageUsers_NoUsers_ReturnsEmptyList() throws Exception {
        when(adminService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"));
    }

    // ==================== Activate User Tests ====================

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testActivateUser_Success_RedirectsToUsersPage() throws Exception {
        doNothing().when(adminService).activateUser(eq(1L));

        mockMvc.perform(post("/admin/user/1/activate")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?activated"));

        verify(adminService, times(1)).activateUser(1L);
    }

    // ==================== Deactivate User Tests ====================

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testDeactivateUser_Success_RedirectsToUsersPage() throws Exception {
        doNothing().when(adminService).deactivateUser(eq(1L));

        mockMvc.perform(post("/admin/user/1/deactivate")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?deactivated"));

        verify(adminService, times(1)).deactivateUser(1L);
    }

    // ==================== Authorization Tests ====================

    @Test
    void testDashboard_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public JobService jobService() {
            return mock(JobService.class);
        }

        @Bean
        public AdminService adminService() {
            return mock(AdminService.class);
        }
    }
}
