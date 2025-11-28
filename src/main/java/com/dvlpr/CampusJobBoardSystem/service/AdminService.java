package com.dvlpr.CampusJobBoardSystem.service;

import com.dvlpr.CampusJobBoardSystem.entity.User;
import com.dvlpr.CampusJobBoardSystem.entity.UserStatus;
import com.dvlpr.CampusJobBoardSystem.exception.ResourceNotFoundException;
import com.dvlpr.CampusJobBoardSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for admin user management operations.
 */
@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Get all users. */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Activate a user account. */
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    /** Deactivate a user account. */
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
}
