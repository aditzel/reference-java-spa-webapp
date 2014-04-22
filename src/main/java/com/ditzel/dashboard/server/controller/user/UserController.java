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
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.stormpath.sdk.account.Accounts.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    public void redirectToCurrentUser(HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        response.sendRedirect("/api/user/" + authentication.getName());
    }

    @RequestMapping(value = "{username}")
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
