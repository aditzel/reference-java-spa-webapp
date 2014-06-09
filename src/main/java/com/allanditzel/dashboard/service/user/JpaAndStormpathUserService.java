package com.allanditzel.dashboard.service.user;

import com.allanditzel.dashboard.controller.user.UserBuilder;
import com.allanditzel.dashboard.exception.ApplicationException;
import com.allanditzel.dashboard.exception.UnknownResourceException;
import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.model.User;
import com.allanditzel.dashboard.persistence.StormpathUserMappingRepository;
import com.allanditzel.dashboard.security.Role;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;

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
    private Client client;

    @Value("${stormpath.application.url}")
    private String stormpathApplicationUrl;

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
        Account account = createStormpathAccount(user);
        mapping.setUsername(account.getUsername());
        mapping.setStormpathUrl(account.getHref());
        mapping = userMappingRepo.save(mapping);
        user.setId(mapping.getId());

        return user;
    }

    protected Account createStormpathAccount(User user) {
        Account account = client.instantiate(Account.class);
        account.setUsername(user.getUsername());
        account.setEmail(user.getEmail());
        account.setGivenName(user.getFirstName());
        account.setSurname(user.getLastName());
        account.setPassword(user.getPassword());

        Application application = client.getResource(stormpathApplicationUrl, Application.class);
        GroupList groupList = application.getGroups(Groups.where(Groups.name().eqIgnoreCase(Role.USER.name())));
        Iterator<Group> iterator = groupList.iterator();
        if (!iterator.hasNext()) {
            throw new ApplicationException("Could not find group 'user' to add " + user.getEmail() + " to.");
        }
        Group userGroup = iterator.next();

        account = application.createAccount(account);
        account.addGroup(userGroup);

        return account;
    }

    private User getUser(StormpathUserMapping mapping) {
        if (mapping == null) {
            throw new UnknownResourceException("Mapping not found");
        }
        Account account = getAccountFromStormpath(mapping.getStormpathUrl());
        UserBuilder userBuilder = new UserBuilder();

        return userBuilder.addStormpathAccount(account).addStormpathUserMapping(mapping).build();
    }

    private Account getAccountFromStormpath(String href) {
        Account account = client.getResource(href, Account.class);

        if (account == null) {
            throw new UnknownResourceException("Specified user does not exist in Stormpath.");
        }

        return account;
    }
}
