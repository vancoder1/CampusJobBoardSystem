package com.dvlpr.CampusJobBoardSystem.controller;

import com.dvlpr.CampusJobBoardSystem.dto.UserRegistrationDto;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.security.SecurityConfig;
import com.dvlpr.CampusJobBoardSystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController.
 * Tests authentication-related endpoints: home, login, registration, and dashboard routing.
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // ==================== Home Page Tests ====================

    @Test
    void testHomePage_ReturnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    // ==================== Login Page Tests ====================

    @Test
    void testLoginPage_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // ==================== Registration Tests ====================

    @Test
    void testShowRegistrationForm_ReturnsRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testRegisterUser_Success_RedirectsToLoginWithSuccess() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        doNothing().when(userService).registerUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("password", "password123")
                        .param("role", "STUDENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail_ReturnsRegisterViewWithError() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        when(userService.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("fullName", "John Doe")
                        .param("email", "existing@example.com")
                        .param("password", "password123")
                        .param("role", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void testRegisterUser_InvalidEmail_ReturnsRegisterViewWithError() throws Exception {
        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("fullName", "John Doe")
                        .param("email", "invalid-email")
                        .param("password", "password123")
                        .param("role", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void testRegisterUser_BlankFullName_ReturnsRegisterViewWithError() throws Exception {
        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("fullName", "")
                        .param("email", "test@example.com")
                        .param("password", "password123")
                        .param("role", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void testRegisterUser_ShortPassword_ReturnsRegisterViewWithError() throws Exception {
        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("fullName", "John Doe")
                        .param("email", "test@example.com")
                        .param("password", "short")
                        .param("role", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    void testRegisterUser_MissingRole_ReturnsRegisterViewWithError() throws Exception {
        mockMvc.perform(post("/saveUser")
                        .with(csrf())
                        .param("fullName", "John Doe")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    // ==================== Dashboard Routing Tests ====================

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDashboard_AdminRole_RedirectsToAdminDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void testDashboard_EmployerRole_RedirectsToEmployerDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employer/dashboard"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testDashboard_StudentRole_RedirectsToStudentDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    void testDashboard_Unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }
}
