package com.allanditzel.dashboard.service.account;

import com.allanditzel.dashboard.exception.ApplicationException;
import com.allanditzel.dashboard.exception.UnknownResourceException;
import com.allanditzel.dashboard.model.User;
import com.allanditzel.dashboard.security.Role;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * Stormpath spcific implementation of {@link com.allanditzel.dashboard.service.account.AccountService}
 */
@Service
public class StormpathAccountService implements AccountService {
    @Autowired
    private Client client;

    @Value("${stormpath.application.url}")
    private String stormpathApplicationUrl;

    @Override
    public Account getAccountByUsername(String username) {
        Application application = client.getResource(stormpathApplicationUrl, Application.class);
        AccountList accountList = application.getAccounts(Accounts.where(Accounts.username().eqIgnoreCase(username)));

        Iterator<Account> iterator = accountList.iterator();
        if (iterator == null || !iterator.hasNext()) {
            throw new UnknownResourceException("Cannot find Account in Stormpath with the specified username " + username);
        }

        return iterator.next();
    }

    @Override
    public Account getAccountByUrl(String url) {
        Account account = client.getResource(url, Account.class);

        if (account == null) {
            throw new UnknownResourceException("Specified user does not exist in Stormpath.");
        }

        return account;
    }

    @Override
    public Account createAccountFromUser(User user) {
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
}
