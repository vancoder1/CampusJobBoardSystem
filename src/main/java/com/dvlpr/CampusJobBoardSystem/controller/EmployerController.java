package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.service.ApplicationService;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for employer-specific operations.
 * Handles job posting, editing, deletion, and viewing applications.
 */
@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    public EmployerController(JobService jobService, ApplicationService applicationService) {
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    /**
     * Display employer dashboard with their job postings.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("jobs", jobService.getJobsByEmployer(email));
        return "employer/dashboard";
    }

    /**
     * Display form to create a new job.
     */
    @GetMapping("/job/new")
    public String createJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "employer/create-job";
    }

    /**
     * Save a new job posting with validation.
     */
    @PostMapping("/job/save")
    public String saveJob(@Valid @ModelAttribute("job") Job job,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (bindingResult.hasErrors()) {
            return "employer/create-job";
        }
        
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        jobService.postJob(job, email);
        redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/employer/dashboard";
    }

    /**
     * Display form to edit an existing job.
     */
    @GetMapping("/job/{id}/edit")
    public String editJobForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Job job = jobService.getJobById(id);
        
        // Verify this job belongs to the current employer
        if (!job.getEmployer().getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to edit this job.");
            return "redirect:/employer/dashboard";
        }
        
        model.addAttribute("job", job);
        return "employer/edit-job";
    }

    /**
     * Update an existing job posting with validation.
     */
    @PostMapping("/job/{id}/update")
    public String updateJob(@PathVariable Long id,
                            @Valid @ModelAttribute("job") Job updatedJob,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            updatedJob.setId(id);
            return "employer/edit-job";
        }
        
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        jobService.updateJob(id, updatedJob, email);
        redirectAttributes.addFlashAttribute("success", "Job updated successfully!");
        return "redirect:/employer/dashboard";
    }

    /**
     * Delete a job posting.
     */
    @PostMapping("/job/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        jobService.deleteJob(id, email);
        redirectAttributes.addFlashAttribute("success", "Job deleted successfully!");
        return "redirect:/employer/dashboard";
    }

    /**
     * View applications for a specific job.
     */
    @GetMapping("/job/{id}/applications")
    public String viewApplications(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Job job = jobService.getJobById(id);
        
        // Verify this job belongs to the current employer
        if (!job.getEmployer().getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("error", "You are not authorized to view these applications.");
            return "redirect:/employer/dashboard";
        }
        
        model.addAttribute("job", job);
        model.addAttribute("applications", applicationService.getApplicationsForJob(id));
        return "employer/view-applications";
    }
}