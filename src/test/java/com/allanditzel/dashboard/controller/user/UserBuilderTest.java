package com.allanditzel.dashboard.controller.user;

import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.model.User;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserBuilderTest {
    @Mock
    private Account account;

    @Mock
    private StormpathUserMapping stormpathUserMapping;

    @Mock
    private GroupList groupList;

    @Mock
    private Iterator<Group> groupIterator;

    @Mock
    private Group group1;

    @Mock
    private Group group2;

    private UserBuilder builder;

    @Before
    public void setUp() {
        builder = new UserBuilder();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTryingToAddNullAccount() {
        builder.addStormpathAccount(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTryingToAddNullMapping() {
        builder.addStormpathUserMapping(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTryingToBuildWithOnlyAnAccount() {
        builder.addStormpathAccount(account).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfTryingToBuildWithOnlyAMapping() {
        builder.addStormpathUserMapping(stormpathUserMapping).build();
    }

    @Test
    public void shouldCreateUser() {
        String id = "id";
        String username = "username";
        String email = "email";
        String firstName = "firstName";
        String lastName = "lastName";
        String group1Name = "group1";
        String group2Name = "group2";
        String[] groupNames = {group1Name, group2Name};
        Set<String> groups = new HashSet<>();
        Collections.addAll(groups, groupNames);

        when(stormpathUserMapping.getId()).thenReturn(id);
        when(stormpathUserMapping.getUsername()).thenReturn(username);
        when(account.getEmail()).thenReturn(email);
        when(account.getGivenName()).thenReturn(firstName);
        when(account.getSurname()).thenReturn(lastName);
        when(account.getGroups()).thenReturn(groupList);
        when(groupList.iterator()).thenReturn(groupIterator);
        when(groupIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(groupIterator.next()).thenReturn(group1).thenReturn(group2);
        when(group1.getName()).thenReturn(group1Name);
        when(group2.getName()).thenReturn(group2Name);

        User user = builder.addStormpathUserMapping(stormpathUserMapping).addStormpathAccount(account).build();

        verify(stormpathUserMapping).getId();
        verify(stormpathUserMapping).getUsername();
        verify(account).getEmail();
        verify(account).getGivenName();
        verify(account).getSurname();
        verify(account).getGroups();
        verify(groupList).iterator();
        verify(groupIterator, times(3)).hasNext();
        verify(groupIterator, times(2)).next();
        verify(group1).getName();
        verify(group2).getName();

        assertNotNull(user);
        assertEquals(id, user.getId());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(groups, user.getRoles());
    }
}