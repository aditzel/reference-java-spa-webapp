package com.allanditzel.dashboard.persistence;

import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.test.AbstractDaoTest;
import com.allanditzel.dashboard.test.annotation.Dao;
import com.allanditzel.dashboard.test.annotation.DataSet;
import com.google.common.collect.Iterables;
import org.junit.Test;

import java.util.Iterator;

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

    @DataSet("classpath:dbunit/stormpath_user_mapping.xml")
    @Test
    public void testFindAll() {
        Iterable<StormpathUserMapping> mappings = repository.findAll();
        assertEquals(2, Iterables.size(mappings));

        Iterator<StormpathUserMapping> iterator = mappings.iterator();
        assertEquals("test-1", iterator.next().getId());
        assertEquals("test-2", iterator.next().getId());
    }

    @DataSet("classpath:dbunit/stormpath_user_mapping.xml")
    @Test
    public void testFindByUsername() {
        assertEquals("test-1", repository.findByUsernameIgnoreCase("aditzel").getId());
        assertEquals("test-2", repository.findByUsernameIgnoreCase("bturner").getId());
        assertNull(repository.findByUsernameIgnoreCase("jdoe"));
    }

    @DataSet("classpath:dbunit/stormpath_user_mapping.xml")
    @Test
    public void testFindByStormpathUrl() {
        assertEquals("test-1", repository.findByStormpathUrlIgnoreCase("http://example.com").getId());
        assertEquals("test-2", repository.findByStormpathUrlIgnoreCase("http://test.com").getId());
        assertNull(repository.findByStormpathUrlIgnoreCase("http://some.url/"));
    }
}
