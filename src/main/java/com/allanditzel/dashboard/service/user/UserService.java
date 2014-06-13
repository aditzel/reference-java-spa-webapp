package com.allanditzel.dashboard.service.user;

import com.allanditzel.dashboard.model.User;

import java.util.List;

/**
 * @author Allan Ditzel
 * @since 1.0
 */
public interface UserService {
    public User getById(String id);

    public User getByHref(String href);

    public User getByUsername(String username);

    public User createUser(User user);

    public List<User> getAllUsers();
}
