package com.ditzel.dashboard.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents user in the system.
 */
public class User {
    Set<String> roles;

    public User() {
        roles = new HashSet<>();
    }

    public void addRole(String role) {
        roles.add(role);
    }
}
