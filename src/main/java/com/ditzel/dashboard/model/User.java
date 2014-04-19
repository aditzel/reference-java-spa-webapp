package com.ditzel.dashboard.model;

import org.springframework.hateoas.ResourceSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents user in the system.
 */
public class User extends ResourceSupport {
    Set<String> roles;

    public User() {
        roles = new HashSet<>();
    }

    public void addRole(String role) {
        roles.add(role);
    }
}
