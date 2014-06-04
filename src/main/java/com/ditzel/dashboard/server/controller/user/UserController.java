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

package com.ditzel.dashboard.server.controller.user;

import com.ditzel.dashboard.model.resource.UserResource;
import com.ditzel.dashboard.model.resource.UserResourceAssembler;
import com.ditzel.dashboard.server.Constants;
import com.ditzel.dashboard.server.exception.ApplicationException;
import com.ditzel.dashboard.server.exception.UnknownResourceException;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.group.Groups;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Iterator;

import static com.stormpath.sdk.account.Accounts.username;
import static com.stormpath.sdk.account.Accounts.where;

/**
 * Exposes {@link com.ditzel.dashboard.model.resource.UserResource} via a HATEOAS compliant REST API.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@RequestMapping("/api/user")
@Controller
public class UserController {
    @Autowired
    private Client client;

    @Autowired
    private UserResourceAssembler resourceAssembler;

    @Value("${stormpath.application.url}")
    private String stormpathApplicationUrl;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public String redirectToCurrentUser() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "redirect:/api/user/" + authentication.getName();
    }

    @RequestMapping(value = "{username}", method = RequestMethod.GET)
    @ResponseBody
    public UserResource getUser(@PathVariable("username") String username) {

        Application application = client.getResource(stormpathApplicationUrl, Application.class);
        AccountList accountList = application.getAccounts(where(username().eqIgnoreCase(username)));

        Account requestedUser = null;

        for (Account account : accountList) {
            if (account.getUsername().equals(username)) {
                requestedUser = account;
                break;
            }
        }

        if (requestedUser == null) {
            throw new UnknownResourceException("Specified user [" + username + "] not found.");
        }

        return resourceAssembler.toResource(requestedUser);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public UserResource createUser(@RequestBody UserResource userResource) {
        Application application = client.getResource(stormpathApplicationUrl, Application.class);
        GroupList groupList = application.getGroups(Groups.where(Groups.name().eqIgnoreCase("user")));
        Iterator<Group> iterator = groupList.iterator();
        if (!iterator.hasNext()) {
            throw new ApplicationException("Could not find group 'user' to add " + userResource.getEmail() + " to.");
        }
        Group userGroup = iterator.next();
        Account newAccount = createNewAccountInstanceWithRandomPassword(userResource);
        newAccount = application.createAccount(newAccount);
        newAccount.addGroup(userGroup);
        newAccount.save();
        return resourceAssembler.toResource(newAccount);
    }

    protected Account createNewAccountInstanceWithRandomPassword(UserResource userResource) {
        Account newAccount = client.instantiate(Account.class);
        newAccount.setGivenName(userResource.getFirstName());
        newAccount.setSurname(userResource.getLastName());
        newAccount.setUsername(userResource.getUsername());
        newAccount.setEmail(userResource.getEmail());
        newAccount.setPassword(RandomStringUtils.random(64, true, true));
        return  newAccount;
    }
}
