package com.dvlpr.CampusJobBoardSystem.security;

import com.dvlpr.CampusJobBoardSystem.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation that wraps the User entity.
 * Provides user authentication information to Spring Security.
 */
public record CustomUserDetails(User user) implements UserDetails {

    /**
     * Creates a CustomUserDetails instance wrapping the given user.
     *
     * @param user the User entity to wrap
     */
    public CustomUserDetails {
    }

    /**
     * Returns the authorities granted to the user.
     * Maps the user's role to a Spring Security authority with ROLE_ prefix.
     *
     * @return collection containing the user's authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username (email) used for authentication.
     *
     * @return the user's email address
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Returns the underlying User entity.
     * Useful for accessing user data in controllers.
     *
     * @return the User entity
     */
    @Override
    public User user() {
        return user;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}