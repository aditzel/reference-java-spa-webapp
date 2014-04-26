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

import com.ditzel.dashboard.model.UserResource;
import com.ditzel.dashboard.model.UserResourceAssembler;
import com.ditzel.dashboard.server.Constants;
import com.ditzel.dashboard.server.exception.UnknownResourceException;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import static com.stormpath.sdk.account.Accounts.username;
import static com.stormpath.sdk.account.Accounts.where;

/**
 * Exposes {@link com.ditzel.dashboard.model.UserResource} via a HATEOAS compliant REST API.
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

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public String redirectToCurrentUser() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "redirect:/api/user/" + authentication.getName();
    }

    @RequestMapping(value = "{username}", method = RequestMethod.GET)
    @ResponseBody
    public UserResource getUser(@PathVariable("username") String username) {

        Application application = client.getResource(Constants.STORMPATH_APPLICATION_URL, Application.class);
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
}
