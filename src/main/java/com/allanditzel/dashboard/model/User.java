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

package com.allanditzel.dashboard.model;

import com.allanditzel.dashboard.model.resource.UserResource;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a User.
 */
public class User {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<String> roles;

    public User() {
        roles = new HashSet<>();
    }

    public User(UserResource resource) {
        this();
        this.username = resource.getUsername();
        this.firstName = resource.getFirstName();
        this.lastName = resource.getLastName();
        this.email = resource.getEmail();
        this.password = resource.getPassword();
    }

    public User(Account account, StormpathUserMapping stormpathUserResourceMapping) {
        this(account);
        this.id = stormpathUserResourceMapping.getId();
    }

    public User(Account account) {
        this();
        this.username = account.getUsername();
        this.firstName = account.getGivenName();
        this.lastName = account.getSurname();
        this.email = account.getEmail();

        GroupList groupList = account.getGroups();

        for (Group aGroupList : groupList) {
            roles.add(aGroupList.getName());
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
