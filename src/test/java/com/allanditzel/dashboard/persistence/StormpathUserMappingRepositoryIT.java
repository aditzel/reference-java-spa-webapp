package com.allanditzel.dashboard.persistence;

import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.test.AbstractDaoTest;
import com.allanditzel.dashboard.test.annotation.Dao;
import org.junit.Test;

/**
 * Integration
 */
public class StormpathUserMappingRepositoryIT extends AbstractDaoTest {

    @Dao
    private StormpathUserMappingRepository repository;

    @Test
    public void shouldInsertAndFindByAttributesCorrectly() {
        String username = "aditzel";
        String url = "http://some.url/";

        StormpathUserMapping expectedMapping = new StormpathUserMapping(username, url);
        assertNull(expectedMapping.getId());
        StormpathUserMapping savedMapping = repository.save(expectedMapping);

        assertNotNull(savedMapping.getId());
        StormpathUserMapping actualMapping = repository.findOne(savedMapping.getId());
        assertEquals(savedMapping.getId(), actualMapping.getId());
        assertEquals(savedMapping.getUsername(), actualMapping.getUsername());
        assertEquals(savedMapping.getStormpathUrl(), actualMapping.getStormpathUrl());
        actualMapping = repository.findByStormpathUrlIgnoreCase(url);
        assertEquals(savedMapping.getId(), actualMapping.getId());
        assertEquals(savedMapping.getUsername(), actualMapping.getUsername());
        assertEquals(savedMapping.getStormpathUrl(), actualMapping.getStormpathUrl());
        actualMapping = repository.findByUsernameIgnoreCase(username);
        assertNotNull(actualMapping.getId());
        assertEquals(username, actualMapping.getUsername());
        assertEquals(url, actualMapping.getStormpathUrl());
    }
}
