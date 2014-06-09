package com.allanditzel.dashboard.controller.user;

import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.model.User;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * Builder for {@link com.allanditzel.dashboard.model.User} objects.
 *
 * @since 1.0
 */
public class UserBuilder {
    private Account stormpathAccount;
    private StormpathUserMapping stormpathUserMapping;

    public UserBuilder addStormpathAccount(Account account) {
        Assert.notNull(account, "Account parameter cannot be null.");
        this.stormpathAccount = account;

        return this;
    }

    public UserBuilder addStormpathUserMapping(StormpathUserMapping stormpathUserMapping) {
        Assert.notNull(stormpathUserMapping, "StormpathUserMapping parameter cannot be null.");
        this.stormpathUserMapping = stormpathUserMapping;

        return this;
    }

    public User build() {
        Assert.notNull(this.stormpathAccount, "Stormpath Account is null. Please call addStormpathAccount() before calling build().");
        Assert.notNull(this.stormpathUserMapping, "Stormpath User Mapping is null. Please call addStormpathUserMapping() ");

        User user = new User();
        user.setId(stormpathUserMapping.getId());
        user.setUsername(stormpathUserMapping.getUsername());
        user.setEmail(stormpathAccount.getEmail());
        user.setFirstName(stormpathAccount.getGivenName());
        user.setLastName(stormpathAccount.getSurname());
        user.setRoles(getRolesFromStormpathGroups(stormpathAccount));

        return user;
    }

    private Set<String> getRolesFromStormpathGroups(Account account) {
        Set<String> roles = new HashSet<>();
        GroupList groupList = account.getGroups();
        if (groupList != null) {
            for (Group group : groupList) {
                roles.add(group.getName());
            }
        }
        return roles;
    }
}
