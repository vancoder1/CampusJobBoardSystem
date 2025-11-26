package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.Job;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.service.ApplicationService;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import com.dvlpr.CampusJobBoardSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // Get jobs posted by this specific employer
        model.addAttribute("jobs", jobService.getJobsByEmployer(email));
        return "employer/dashboard";
    }

    @GetMapping("/job/new")
    public String createJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "employer/create-job";
    }

    @PostMapping("/job/save")
    public String saveJob(@ModelAttribute("job") Job job) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        jobService.postJob(job, email);
        return "redirect:/employer/dashboard?success";
    }

    @GetMapping("/job/{id}/applications")
    public String viewApplications(@PathVariable Long id, Model model) {
        // In a real app, verify that the job belongs to this employer first!
        model.addAttribute("applications", applicationService.getApplicationsForJob(id));
        return "employer/view-applications";
    }
}