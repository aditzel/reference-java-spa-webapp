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

import java.util.ArrayList;
import java.util.List;

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
        Account account = accountService.createAccountFromUser(user);
        if (account == null) {
            throw new ApplicationException("Could not create user " + user.getUsername() + " in Stormpath.");
        }
        StormpathUserMapping mapping = new StormpathUserMapping(account.getUsername(), account.getHref());
        mapping = userMappingRepo.save(mapping);
        user.setId(mapping.getId());

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        List<Account> accounts = accountService.getAllAccounts();

        for (Account account : accounts) {
            users.add(getUser(account));
        }

        return users;
    }

    /**
     * Returns a {@link com.allanditzel.dashboard.model.User} object for the given Stormpath {@code Account}.
     *
     * @param account the Stormpath account we want a {@link com.allanditzel.dashboard.model.User} for
     * @return the {@link com.allanditzel.dashboard.model.User} representing the Stormpath {@code Account}.
     */
    private User getUser(Account account) {
        Assert.notNull(account, "Account object cannot be null.");
        UserBuilder userBuilder = new UserBuilder();
        StormpathUserMapping mapping = userMappingRepo.findByUsernameIgnoreCase(account.getUsername());
        if (mapping == null) {
            mapping = new StormpathUserMapping(account.getUsername(), account.getHref());
            mapping = userMappingRepo.save(mapping);
        }

        return userBuilder.addStormpathAccount(account).addStormpathUserMapping(mapping).build();
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
