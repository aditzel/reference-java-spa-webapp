package com.allanditzel.dashboard.service.user;

import com.allanditzel.dashboard.exception.ApplicationException;
import com.allanditzel.dashboard.exception.UnknownResourceException;
import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.model.User;
import com.allanditzel.dashboard.persistence.StormpathUserMappingRepository;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JpaAndStormpathUserServiceTest {
    private static final String STORMPATH_APP_URL = "http://some.url";
    private static final String STORMPATH_USER_URL = "http://user.url";
    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PASSWORD = "password";
    private static final String ID = "ID";

    private JpaAndStormpathUserService service;

    @Mock
    private Client stormpathClient;

    @Mock
    private StormpathUserMappingRepository userMappingRepo;

    @Mock
    private StormpathUserMapping mapping;

    @Mock
    private Account account;

    @Mock
    private Application application;

    @Mock
    private GroupList groupList;

    @Mock
    private Iterator<Group> groupIterator;

    @Mock
    private Group group;

    @Before
    public void setUp() throws Exception {
        service = new JpaAndStormpathUserService();
        ReflectionTestUtils.setField(service, "client", stormpathClient);
        ReflectionTestUtils.setField(service, "stormpathApplicationUrl", STORMPATH_APP_URL);
        ReflectionTestUtils.setField(service, "userMappingRepo", userMappingRepo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalAccessExceptionWhenNullId() {
        service.getById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalAccessExceptionWhenEmptyId() {
        service.getById("");
    }

    @Test(expected = UnknownResourceException.class)
    public void shouldThrowUnknownResourceExceptionIfIdNotKnown() {
        String invalidId = "1";

        when(userMappingRepo.findOne(invalidId)).thenReturn(null);

        service.getById(invalidId);
    }

    @Test(expected = UnknownResourceException.class)
    public void shouldThrowUnknownResourceExceptionIfCannotFindInStormpath() {
        String validId = "1";

        when(userMappingRepo.findOne(validId)).thenReturn(mapping);
        when(mapping.getStormpathUrl()).thenReturn(STORMPATH_USER_URL);
        when(stormpathClient.getResource(STORMPATH_USER_URL, Account.class)).thenReturn(null);

        service.getById(validId);
    }

    @Test
    public void shouldReturnUserWithValidId() {
        String validId = "1";

        when(userMappingRepo.findOne(validId)).thenReturn(mapping);
        when(mapping.getStormpathUrl()).thenReturn(STORMPATH_USER_URL);
        when(stormpathClient.getResource(STORMPATH_USER_URL, Account.class)).thenReturn(account);

        service.getById(validId);

        verify(userMappingRepo).findOne(validId);
        verify(mapping).getStormpathUrl();
        verify(stormpathClient).getResource(STORMPATH_USER_URL, Account.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalAccessExceptionWhenNullHref() {
        service.getByHref(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalAccessExceptionWhenEmptyHref() {
        service.getByHref("");
    }

    @Test(expected = UnknownResourceException.class)
    public void shouldThrowUnknownResourceExceptionIfHrefNotKnown() {
        String href = "http://href.com";

        when(userMappingRepo.findByStormpathUrlIgnoreCase(href)).thenReturn(null);

        service.getById(href);
    }

    @Test
    public void shouldReturnUserWithValidHref() {
        String href = "http://href.com";

        when(userMappingRepo.findByStormpathUrlIgnoreCase(href)).thenReturn(mapping);
        when(mapping.getStormpathUrl()).thenReturn(STORMPATH_USER_URL);
        when(stormpathClient.getResource(STORMPATH_USER_URL, Account.class)).thenReturn(account);

        service.getByHref(href);

        verify(userMappingRepo).findByStormpathUrlIgnoreCase(href);
        verify(mapping).getStormpathUrl();
        verify(stormpathClient).getResource(STORMPATH_USER_URL, Account.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalAccessExceptionWhenNullUsername() {
        service.getByUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalAccessExceptionWhenEmptyUsername() {
        service.getByUsername("");
    }

    @Test(expected = UnknownResourceException.class)
    public void shouldThrowUnknownResourceExceptionIfUsernameNotKnown() {
        String username = "testuser";

        when(userMappingRepo.findByUsernameIgnoreCase(username)).thenReturn(null);

        service.getByUsername(username);
    }

    @Test
    public void shouldReturnUserWithValidUsername() {
        String username = "testuser";

        when(userMappingRepo.findByUsernameIgnoreCase(username)).thenReturn(mapping);
        when(mapping.getStormpathUrl()).thenReturn(STORMPATH_USER_URL);
        when(stormpathClient.getResource(STORMPATH_USER_URL, Account.class)).thenReturn(account);

        service.getByUsername(username);

        verify(userMappingRepo).findByUsernameIgnoreCase(username);
        verify(mapping).getStormpathUrl();
        verify(stormpathClient).getResource(STORMPATH_USER_URL, Account.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfUserIsNull() {
        service.createUser(null);
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowApplicationExceptionIfUserGroupNotFound() {
        User user = createTestUser();
        setUpStormpathClientAndGroups();

        when(groupIterator.hasNext()).thenReturn(false);

        service.createUser(user);
    }

    @Test
    public void shouldCreateUser() {
        User user = createTestUser();
        setUpStormpathClientAndGroups();

        when(groupIterator.hasNext()).thenReturn(true);
        when(groupIterator.next()).thenReturn(group);
        when(application.createAccount(account)).thenReturn(account);
        when(account.getUsername()).thenReturn(USERNAME);
        when(account.getHref()).thenReturn(STORMPATH_USER_URL);
        when(userMappingRepo.save(any(StormpathUserMapping.class))).thenReturn(mapping);
        when(mapping.getId()).thenReturn(ID);

        service.createUser(user);

        verify(stormpathClient).instantiate(Account.class);
        verify(stormpathClient).getResource(STORMPATH_APP_URL, Application.class);
        verify(application).getGroups(any(GroupCriteria.class));
        verify(groupList).iterator();
        verify(groupIterator).hasNext();
        verify(groupIterator).next();
        verify(application).createAccount(account);
        verify(account).getUsername();
        verify(account).getHref();
        verify(userMappingRepo).save(any(StormpathUserMapping.class));
        verify(mapping).getId();
    }

    private void setUpStormpathClientAndGroups() {
        when(stormpathClient.instantiate(Account.class)).thenReturn(account);
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(application);
        when(application.getGroups(any(GroupCriteria.class))).thenReturn(groupList);
        when(groupList.iterator()).thenReturn(groupIterator);
    }

    private User createTestUser() {

        User user = new User();
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setPassword(PASSWORD);

        return user;
    }
}