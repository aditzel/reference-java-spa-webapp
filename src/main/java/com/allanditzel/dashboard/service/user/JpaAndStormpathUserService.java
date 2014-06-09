package com.allanditzel.dashboard.service.user;

import com.allanditzel.dashboard.controller.user.UserBuilder;
import com.allanditzel.dashboard.exception.ApplicationException;
import com.allanditzel.dashboard.exception.UnknownResourceException;
import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.model.User;
import com.allanditzel.dashboard.persistence.StormpathUserMappingRepository;
import com.allanditzel.dashboard.service.account.AccountService;
import com.stormpath.sdk.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * JPA and Stormpath specific implementation of {@link com.allanditzel.dashboard.service.user.UserService}.
 *
 * @since 1.0
 */
@Service
public class JpaAndStormpathUserService implements UserService {
    @Autowired
    private StormpathUserMappingRepository userMappingRepo;

    @Autowired
    private AccountService accountService;

    @Override
    public User getById(String id) {
        Assert.hasText(id);
        StormpathUserMapping mapping = userMappingRepo.findOne(id);
        return getUser(mapping);
    }

    @Override
    public User getByHref(String href) {
        Assert.hasText(href);
        StormpathUserMapping mapping = userMappingRepo.findByStormpathUrlIgnoreCase(href);
        return getUser(mapping);
    }

    @Override
    public User getByUsername(String username) {
        Assert.hasText(username);
        StormpathUserMapping mapping = userMappingRepo.findByUsernameIgnoreCase(username);
        return getUser(mapping);
    }

    @Override
    public User createUser(User user) {
        Assert.notNull(user, "User parameter cannot be null.");
        StormpathUserMapping mapping = new StormpathUserMapping();
        Account account = accountService.createAccountFromUser(user);
        if (account == null) {
            throw new ApplicationException("Could not create user " + user.getUsername() + " in Stormpath.");
        }
        mapping.setUsername(account.getUsername());
        mapping.setStormpathUrl(account.getHref());
        mapping = userMappingRepo.save(mapping);
        user.setId(mapping.getId());

        return user;
    }


    private User getUser(StormpathUserMapping mapping) {
        if (mapping == null) {
            throw new UnknownResourceException("Mapping not found");
        }
        Account account = accountService.getAccountByUrl(mapping.getStormpathUrl());
        if (account == null) {
            throw new UnknownResourceException("Acount not find account in Stormpath.");
        }
        UserBuilder userBuilder = new UserBuilder();

        return userBuilder.addStormpathAccount(account).addStormpathUserMapping(mapping).build();
    }
}
