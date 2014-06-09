package com.allanditzel.dashboard.service.user;

import com.allanditzel.dashboard.model.User;

/**
 * Created by Allan on 6/3/2014.
 */
public interface UserService {
    public User getById(String id);

    public User getByHref(String href);

    public User getByUsername(String username);

    public User createUser(User user);
}
