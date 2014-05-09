/*
 * Copyright 2014 Allan Ditzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ditzel.dashboard.model.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents user resource for the REST API.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
public class UserResource extends ResourceSupport {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> roles;

    public UserResource() {

    }

    public UserResource(String username, String firstName, String lastName, String email, String... roles) {
        this.roles = new HashSet<>();

        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        for (String role : roles) {
            addRole(role);
        }
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
