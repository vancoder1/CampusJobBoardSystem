package com.dvlpr.CampusJobBoardSystem.security;

import com.dvlpr.CampusJobBoardSystem.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map our Enum Role to Spring Security Authority
        // Spring Security expects roles to start with "ROLE_" usually,
        // but we can also check authorities directly. Let's stick to standard convention.
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // We log in with Email
    }

    // Helper method to access the User entity in controllers later
    public User getUser() {
        return user;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        // Could check UserStatus.ACTIVE here if you want to block inactive users
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}