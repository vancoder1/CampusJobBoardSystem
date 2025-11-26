package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.dto.UserRegistrationDto;
import com.dvlpr.CampusJobBoardSystem.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // 1. Home Page
    @GetMapping("/")
    public String home() {
        return "index"; // We will create index.html in Phase 6
    }

    // 2. Login Page
    @GetMapping("/login")
    public String login() {
        return "login"; // We will create login.html
    }

    // 3. Register Page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "register";
    }

    // 4. Handle Registration Form Submission
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

    // 5. Central Dashboard Redirect
    // This method decides where to send the user based on their Role
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