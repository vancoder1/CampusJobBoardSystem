package com.dvlpr.CampusJobBoardSystem.service;

import com.dvlpr.CampusJobBoardSystem.dto.UserRegistrationDto;
import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.entity.UserRole;
import com.dvlpr.CampusJobBoardSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user
    public void registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
                        throw new RuntimeException("Email is already in use. Please use a different email address or try logging in.");
        }

        User user = new User();
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail());
        user.setRole(registrationDto.getRole());
        // Encrypt password immediately (Requirement 2.1)
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}