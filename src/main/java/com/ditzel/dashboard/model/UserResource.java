package com.ditzel.dashboard.model;

import org.springframework.hateoas.ResourceSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents user in the system.
 */
public class UserResource extends ResourceSupport {
    private String username;
    private Set<String> roles;

    public UserResource(String username) {
        roles = new HashSet<>();
        this.username = username;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getUsername() {
        return username;
    }
}
