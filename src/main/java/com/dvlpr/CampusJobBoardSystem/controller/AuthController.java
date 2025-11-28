package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.dto.UserRegistrationDto;
import com.dvlpr.CampusJobBoardSystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for authentication-related operations.
 * Handles login, registration, and dashboard routing.
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Display home page.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Display login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Display registration form.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    /**
     * Handle registration form submission.
     * Validates input and creates new user account.
     */
    @PostMapping("/saveUser")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                               BindingResult result,
                               Model model) {
        // Check for existing email (Custom validation)
        if (userService.findByEmail(userDto.getEmail()).isPresent()) {
            result.rejectValue("email", null, "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "register";
        }

        userService.registerUser(userDto);
        return "redirect:/login?success";
    }

    /**
     * Central dashboard redirect.
     * Routes users to their role-specific dashboard.
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (request.isUserInRole("ROLE_EMPLOYER")) {
            return "redirect:/employer/dashboard";
        } else if (request.isUserInRole("ROLE_STUDENT")) {
            return "redirect:/student/dashboard";
        }
        return "redirect:/";
    }
}