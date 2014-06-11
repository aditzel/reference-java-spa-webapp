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

package com.allanditzel.dashboard.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Entity class designed to keep track of Stormpath specific data in order to mask its use.
 */
@Entity
@Table(name = "stormpath_user_mapping", indexes = {
        @Index(name = "idx_spum_username", columnList = "user_name"),
        @Index(name = "idx_spum_url", columnList = "stormpath_url"),
})
public class StormpathUserMapping {

    @Column(name = "entity_id", nullable = false, unique = true)
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;
    @Column(name = "user_name")
    private String username;
    @Column(name = "stormpath_url")
    private String stormpathUrl;

    @SuppressWarnings("unused") //Required for proxying
    protected StormpathUserMapping() {}

    public StormpathUserMapping(String username, String stormpathUrl) {
        this.username = username;
        this.stormpathUrl = stormpathUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStormpathUrl() {
        return stormpathUrl;
    }

    public void setStormpathUrl(String stormpathUrl) {
        this.stormpathUrl = stormpathUrl;
    }
}
