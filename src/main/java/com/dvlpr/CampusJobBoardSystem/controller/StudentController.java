package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import com.dvlpr.CampusJobBoardSystem.exception.DuplicateApplicationException;
import com.dvlpr.CampusJobBoardSystem.service.ApplicationService;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for student-specific operations.
 * Handles job browsing, job details viewing, and job applications.
 */
@Controller
@RequestMapping("/student")
public class StudentController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    public StudentController(JobService jobService, ApplicationService applicationService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    /**
     * Display student dashboard with approved jobs.
     */
    @GetMapping("/dashboard")
    public String listJobs(Model model) {
        // Requirement: View only admin-approved jobs
        model.addAttribute("jobs", jobService.getAllApprovedJobs());
        model.addAttribute("categories", jobService.getAvailableCategories());
        model.addAttribute("locations", jobService.getAvailableLocations());
        return "student/dashboard";
    }

    /**
     * Search and filter jobs.
     * Supports keyword search and filtering by category or location.
     */
    @GetMapping("/search")
    public String searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            Model model) {

        java.util.List<Job> jobs;

        // Priority: keyword search > category filter > location filter
        if (keyword != null && !keyword.trim().isEmpty()) {
            jobs = jobService.searchApprovedJobs(keyword);
            model.addAttribute("searchKeyword", keyword);
        } else if (category != null && !category.trim().isEmpty()) {
            jobs = jobService.getApprovedJobsByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else if (location != null && !location.trim().isEmpty()) {
            jobs = jobService.getApprovedJobsByLocation(location);
            model.addAttribute("selectedLocation", location);
        } else {
            jobs = jobService.getAllApprovedJobs();
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("categories", jobService.getAvailableCategories());
        model.addAttribute("locations", jobService.getAvailableLocations());

        return "student/dashboard";
    }

    /**
     * View detailed job description.
     */
    @GetMapping("/job/{id}")
    public String viewJobDetails(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id);
        
        // Only show approved jobs to students
        if (job.getStatus() != JobStatus.APPROVED) {
            return "redirect:/student/dashboard?error=Job not available";
        }
        
        model.addAttribute("job", job);
        return "student/job-details";
    }

    /**
     * View student's applications.
     */
    @GetMapping("/my-applications")
    public String myApplications(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("applications", applicationService.getStudentApplications(email));
        return "student/my-applications";
    }

    /**
     * Apply for a job.
     */
    @PostMapping("/job/{id}/apply")
    public String applyJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            applicationService.applyForJob(id, email);
            redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully!");
            return "redirect:/student/dashboard";
        } catch (DuplicateApplicationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/student/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to apply: " + e.getMessage());
            return "redirect:/student/dashboard";
        }
    }
}