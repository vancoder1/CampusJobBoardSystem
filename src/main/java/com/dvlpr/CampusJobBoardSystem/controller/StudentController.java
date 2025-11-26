package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.service.ApplicationService;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String listJobs(Model model) {
        // Requirement: View only admin-approved jobs
        model.addAttribute("jobs", jobService.getAllApprovedJobs());
        return "student/dashboard";
    }

    @GetMapping("/my-applications")
    public String myApplications(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("applications", applicationService.getStudentApplications(email));
        return "student/my-applications";
    }

    @PostMapping("/job/{id}/apply")
    public String applyJob(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            applicationService.applyForJob(id, email);
            return "redirect:/student/dashboard?applied";
        } catch (RuntimeException e) {
            return "redirect:/student/dashboard?error=" + e.getMessage();
        }
    }
}