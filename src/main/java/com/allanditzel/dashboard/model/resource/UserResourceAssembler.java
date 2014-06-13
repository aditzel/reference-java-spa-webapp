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

package com.allanditzel.dashboard.model.resource;

import com.allanditzel.dashboard.controller.user.UserController;
import com.allanditzel.dashboard.model.User;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates the creation of {@link com.allanditzel.dashboard.model.resource.UserResource} classes for the {@link com.allanditzel.dashboard.controller.user.UserController}
 * using Stormpath {@link com.stormpath.sdk.account.Account} entities.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {
    public UserResourceAssembler() {
        super(UserController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User entity) {
        return createResourceWithId(entity.getId(), entity);
    }

    @Override
    protected UserResource instantiateResource(User entity) {
        return new UserResource(entity.getUsername(), entity.getFirstName(), entity.getLastName(), entity.getEmail(), null, entity.getRoles());
    }
}
