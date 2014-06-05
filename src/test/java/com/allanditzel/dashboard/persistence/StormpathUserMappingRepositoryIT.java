package com.allanditzel.dashboard.persistence;

import com.allanditzel.dashboard.config.TestsJpaConfig;
import com.allanditzel.dashboard.model.StormpathUserMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestsJpaConfig.class })
@Transactional
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = false)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class StormpathUserMappingRepositoryIT {
    @Autowired
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
