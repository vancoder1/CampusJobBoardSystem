package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import com.dvlpr.CampusJobBoardSystem.service.AdminService;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for admin-specific operations.
 * Handles job approval/rejection and user management.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final JobService jobService;
    private final AdminService adminService;

    public AdminController(JobService jobService, AdminService adminService) {
        this.jobService = jobService;
        this.adminService = adminService;
    }

    /**
     * Display admin dashboard with all jobs.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Admin sees ALL jobs (Pending, Approved, Rejected)
        model.addAttribute("jobs", jobService.getAllJobs());
        return "admin/dashboard";
    }

    /**
     * Approve a pending job posting.
     */
    @PostMapping("/job/{id}/approve")
    public String approveJob(@PathVariable Long id) {
        jobService.updateJobStatus(id, JobStatus.APPROVED);
        return "redirect:/admin/dashboard?approved";
    }

    /**
     * Reject a pending job posting.
     */
    @PostMapping("/job/{id}/reject")
    public String rejectJob(@PathVariable Long id) {
        jobService.updateJobStatus(id, JobStatus.REJECTED);
        return "redirect:/admin/dashboard?rejected";
    }

    /**
     * Display user management page with all users.
     */
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/users";
    }

    /**
     * Activate a user account.
     */
    @PostMapping("/user/{id}/activate")
    public String activateUser(@PathVariable Long id) {
        adminService.activateUser(id);
        return "redirect:/admin/users?activated";
    }

    /**
     * Deactivate a user account.
     */
    @PostMapping("/user/{id}/deactivate")
    public String deactivateUser(@PathVariable Long id) {
        adminService.deactivateUser(id);
        return "redirect:/admin/users?deactivated";
    }
}