package com.ditzel.dashboard.server.controller.user;

import com.ditzel.dashboard.model.User;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

/**
 * Created by Allan on 4/18/2014.
 */
@RequestMapping("/api/user")
@Controller
public class UserController {
    @Autowired
    Client client;

    @RequestMapping(value = "/current", method = RequestMethod.GET, produces = "application/json", consumes = {"application/json", "text/javascript"})
    public HttpEntity<User> getUser() {
        User user = new User();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> grantedAuthorities =  authentication.getAuthorities();
        for (GrantedAuthority authority : grantedAuthorities) {
            Group group = client.getDataStore().getResource(authority.getAuthority(), Group.class);
            user.addRole(group.getName());
        }
        return new HttpEntity<User>(user);
    }
}
