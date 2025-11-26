package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.entity.JobStatus;
import com.dvlpr.CampusJobBoardSystem.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JobService jobService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Admin sees ALL jobs (Pending, Approved, Rejected)
        model.addAttribute("jobs", jobService.getAllJobs());
        return "admin/dashboard";
    }

    @PostMapping("/job/{id}/approve")
    public String approveJob(@PathVariable Long id) {
        jobService.updateJobStatus(id, JobStatus.APPROVED);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/job/{id}/reject")
    public String rejectJob(@PathVariable Long id) {
        jobService.updateJobStatus(id, JobStatus.REJECTED);
        return "redirect:/admin/dashboard";
    }
}