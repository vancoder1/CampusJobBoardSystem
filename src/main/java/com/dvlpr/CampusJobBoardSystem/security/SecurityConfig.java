package com.dvlpr.CampusJobBoardSystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        // Public Endpoints
                        .requestMatchers("/register", "/saveUser", "/login", "/css/**", "/js/**", "/").permitAll()

                        // Admin Endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Employer Endpoints
                        .requestMatchers("/employer/**").hasRole("EMPLOYER")

                        // Student Endpoints
                        .requestMatchers("/student/**").hasRole("STUDENT")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")             // Custom login page URL
                        .loginProcessingUrl("/login")    // The URL the form submits to
                        .defaultSuccessUrl("/dashboard", true) // Redirect here after success
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}