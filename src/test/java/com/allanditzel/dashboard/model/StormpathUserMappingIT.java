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

import com.allanditzel.dashboard.config.TestsJpaConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.allanditzel.dashboard.persistence.fixture.JpaAssertions.assertTableExists;
import static com.allanditzel.dashboard.persistence.fixture.JpaAssertions.assertTableHasColumn;

/**
 * Integration test for {@link com.allanditzel.dashboard.model.StormpathUserMapping}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestsJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class StormpathUserMappingIT {
    private static final String STORMPATH_USER_MAPPING_TABLE = "STORMPATH_USER_MAPPING";
    private static final String ID_COLUMN = "ID";
    private static final String USERNAME_COLUMN = "USERNAME";
    private static final String STORMPATH_URL_COLUMN = "STORMPATH_URL";

    @Autowired
    private EntityManager entityManager;

    @Test
    public void verifyTableStructure() {
        assertTableExists(entityManager, STORMPATH_USER_MAPPING_TABLE);

        assertTableHasColumn(entityManager, STORMPATH_USER_MAPPING_TABLE, ID_COLUMN);
        assertTableHasColumn(entityManager, STORMPATH_USER_MAPPING_TABLE, USERNAME_COLUMN);
        assertTableHasColumn(entityManager, STORMPATH_USER_MAPPING_TABLE, STORMPATH_URL_COLUMN);
    }
}
