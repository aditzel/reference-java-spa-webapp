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

package com.allanditzel.dashboard.controller.user;

import com.allanditzel.dashboard.model.User;
import com.allanditzel.dashboard.model.resource.UserResource;
import com.allanditzel.dashboard.model.resource.UserResourceAssembler;
import com.allanditzel.dashboard.security.CurrentUser;
import com.allanditzel.dashboard.security.Role;
import com.allanditzel.dashboard.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Exposes {@link com.allanditzel.dashboard.model.resource.UserResource} via a HATEOAS compliant REST API.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@RequestMapping("/api/user")
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserResourceAssembler resourceAssembler;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public String redirectToCurrentUser(@CurrentUser UsernamePasswordAuthenticationToken authentication) throws IOException {
        return "redirect:/api/user/" + authentication.getName();
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ResponseBody
    public UserResource getUser(@PathVariable("id") String id) {
        User user = userService.getById(id);

        return resourceAssembler.toResource(user);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public UserResource createUser(@RequestBody UserResource userResource) {
        User newUser = createUserFromResouce(userResource);
        newUser = userService.createUser(newUser);

        return resourceAssembler.toResource(newUser);
    }

    protected User createUserFromResouce(UserResource userResource) {
        User user = new User();
        user.setFirstName(userResource.getFirstName());
        user.setLastName(userResource.getLastName());
        user.setEmail(userResource.getEmail());
        user.setPassword(userResource.getPassword());

        return user;
    }
}
