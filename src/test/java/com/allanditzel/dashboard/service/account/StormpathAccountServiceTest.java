package com.allanditzel.dashboard.service.account;

import com.allanditzel.dashboard.exception.ApplicationException;
import com.allanditzel.dashboard.exception.UnknownResourceException;
import com.allanditzel.dashboard.model.User;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
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
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StormpathAccountServiceTest {
    private static final String STORMPATH_APP_URL = "http://some.url";

    @Mock
    private Client stormpathClient;

    @Mock
    private Application application;

    @Mock
    private AccountList accountList;

    @Mock
    private Iterator<Account> accountIterator;

    @Mock
    private Account account;

    @Mock
    private GroupList groupList;

    @Mock
    private Iterator<Group> groupIterator;

    @Mock
    private Group group;

    private StormpathAccountService service;

    @Before
    public void setUp() throws Exception {
        service = new StormpathAccountService();
        ReflectionTestUtils.setField(service, "client", stormpathClient);
        ReflectionTestUtils.setField(service, "stormpathApplicationUrl", STORMPATH_APP_URL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfUsernameIsNull() {
        service.getAccountByUsername(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfUsernameIsEmpty() {
        service.getAccountByUsername("");
    }

    @Test
    public void shouldReturnNullIfStormpathApplicationIsNull() {
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(null);

        assertNull(service.getAccountByUsername("testUsername"));

        verify(stormpathClient).getResource(STORMPATH_APP_URL, Application.class);
        verifyNoMoreInteractions(stormpathClient);
    }

    @Test
    public void shouldReturnNullAccountIfNoAccountCanBeFound() {
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(application);
        when(application.getAccounts(any(AccountCriteria.class))).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(false);

        assertNull(service.getAccountByUsername("test"));

        verify(stormpathClient).getResource(STORMPATH_APP_URL, Application.class);
        verify(application).getAccounts(any(AccountCriteria.class));
        verify(accountList).iterator();
        verify(accountIterator).hasNext();
    }

    @Test
    public void shouldReturnAccountWhenAccountCanBeFound() {
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(application);
        when(application.getAccounts(any(AccountCriteria.class))).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(true);
        when(accountIterator.next()).thenReturn(account);

        assertNotNull(service.getAccountByUsername("test"));

        verify(stormpathClient).getResource(STORMPATH_APP_URL, Application.class);
        verify(application).getAccounts(any(AccountCriteria.class));
        verify(accountList).iterator();
        verify(accountIterator).hasNext();
        verify(accountIterator).next();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfUrlIsNull() {
        service.getAccountByUrl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionIfUrlIsEmpty() {
        service.getAccountByUrl("");
    }

    @Test
    public void shouldReturnNullIfAccountIsNull() {
        String url = "url";
        when(stormpathClient.getResource(url, Account.class)).thenReturn(null);

        assertNull(service.getAccountByUrl(url));

        verify(stormpathClient).getResource(url, Account.class);
    }

    @Test
    public void shouldReturnAccountWhenAccountIsFound() {
        String url = "url";
        when(stormpathClient.getResource(url, Account.class)).thenReturn(account);

        assertNotNull(service.getAccountByUrl(url));

        verify(stormpathClient).getResource(url, Account.class);
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowApplicationExceptionIfUserGroupDoesNotExistInStormpath() {
        when(stormpathClient.instantiate(Account.class)).thenReturn(account);
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(application);
        when(application.getGroups(any(GroupCriteria.class))).thenReturn(groupList);
        when(groupList.iterator()).thenReturn(groupIterator);
        when(groupIterator.hasNext()).thenReturn(false);

        service.createAccountFromUser(new User());
    }

    @Test
    public void shouldCreateAccountOnStormpath() {
        when(stormpathClient.instantiate(Account.class)).thenReturn(account);
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(application);
        when(application.getGroups(any(GroupCriteria.class))).thenReturn(groupList);
        when(groupList.iterator()).thenReturn(groupIterator);
        when(groupIterator.hasNext()).thenReturn(true);
        when(groupIterator.next()).thenReturn(group);
        when(application.createAccount(account)).thenReturn(account);

        assertNotNull(service.createAccountFromUser(new User()));

        verify(stormpathClient).instantiate(Account.class);
        verify(stormpathClient).getResource(STORMPATH_APP_URL, Application.class);
        verify(application).getGroups(any(GroupCriteria.class));
        verify(groupList).iterator();
        verify(groupIterator).hasNext();
        verify(groupIterator).next();
        verify(application).createAccount(account);
        verify(account).addGroup(group);
    }
    /*
    public List<Account> getAllAccounts() {
        Application application = client.getResource(stormpathApplicationUrl, Application.class);
        AccountList accountList = application.getAccounts();
        List<Account> accounts = new ArrayList<>();

        if (accountList != null) {
            for (Account account : accountList) {
                accounts.add(account);
            }
        }

        return accounts;
    }
     */
    @Test
    public void shouldReturnEmptyCollectionIfNoAccountsInStormpath() {
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(application);
        when(application.getAccounts()).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(false);

        List<Account> accounts = service.getAllAccounts();
        assertNotNull(accounts);
        assertEquals(0, accounts.size());

        verify(stormpathClient).getResource(STORMPATH_APP_URL, Application.class);
        verify(application).getAccounts();
        verify(accountList).iterator();
        verify(accountIterator).hasNext();
    }

    @Test
    public void shouldReturnFilledCollectionWithStormpathAccounts() {
        when(stormpathClient.getResource(STORMPATH_APP_URL, Application.class)).thenReturn(application);
        when(application.getAccounts()).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(accountIterator.next()).thenReturn(account);

        List<Account> accounts = service.getAllAccounts();
        assertNotNull(accounts);
        assertEquals(1, accounts.size());

        verify(stormpathClient).getResource(STORMPATH_APP_URL, Application.class);
        verify(application).getAccounts();
        verify(accountList).iterator();
        verify(accountIterator, times(2)).hasNext();
        verify(accountIterator).next();
    }
}