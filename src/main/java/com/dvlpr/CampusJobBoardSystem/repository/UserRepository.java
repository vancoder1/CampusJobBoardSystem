package com.dvlpr.CampusJobBoardSystem.repository;

import com.dvlpr.CampusJobBoardSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Required for Authentication
    Optional<User> findByEmail(String email);
}