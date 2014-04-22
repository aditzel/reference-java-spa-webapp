package com.ditzel.dashboard.server.controller.user;

import com.ditzel.dashboard.model.UserResource;
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
 * Created by Allan on 4/18/2014.
 */
@RequestMapping("/api/user")
@Controller
public class UserController {
    @Autowired
    Client client;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public void redirectToCurrentUser(HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        response.sendRedirect("/api/user/" + authentication.getName());
    }

    @RequestMapping(value = "{username}")
    @ResponseBody
    public UserResource getUser(@PathVariable("username") String username) {
        UserResource userResource = new UserResource(username);

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

        GroupList groups = requestedUser.getGroups();

        for (Group group : groups) {
            userResource.addRole(group.getName());
        }

        return userResource;
    }
}
