package com.allanditzel.dashboard.security.handler;

import com.allanditzel.dashboard.exception.ApplicationException;
import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.persistence.StormpathUserMappingRepository;
import com.allanditzel.dashboard.service.account.AccountService;
import com.allanditzel.dashboard.service.user.UserService;
import com.stormpath.sdk.account.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalUserPersistingAuthenticationSuccessHandlerTest {
    @Mock
    private StormpathUserMappingRepository repository;

    @Mock
    private StormpathUserMapping mapping;

    @Mock
    private AccountService accountService;

    @Mock
    private Account account;

    @Mock
    private Authentication authentication;

    private LocalUserPersistingAuthenticationSuccessHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new LocalUserPersistingAuthenticationSuccessHandler();
        ReflectionTestUtils.setField(handler, "stormpathUserMappingRepository", repository);
        ReflectionTestUtils.setField(handler, "accountService", accountService);
    }

    @Test
    public void shouldDoNothingIfMappingIsFound() throws IOException, ServletException {
        String username = "username";

        when(authentication.getName()).thenReturn(username);
        when(repository.findByUsernameIgnoreCase(username)).thenReturn(mapping);

        handler.onAuthenticationSuccess(null, null, authentication);

        verify(authentication).getName();
        verify(repository).findByUsernameIgnoreCase(username);
        verifyNoMoreInteractions(repository, mapping, authentication);
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowApplicationExceptionIfStormpathAccountCantBeFound() throws IOException, ServletException {
        String username = "username";

        when(authentication.getName()).thenReturn(username);
        when(repository.findByUsernameIgnoreCase(username)).thenReturn(null);
        when(accountService.getAccountByUsername(username)).thenReturn(null);

        handler.onAuthenticationSuccess(null, null, authentication);
    }

    @Test
    public void shouldPersistNewMappingIfNotFound() throws IOException, ServletException {
        String username = "username";

        when(authentication.getName()).thenReturn(username);
        when(repository.findByUsernameIgnoreCase(username)).thenReturn(null);
        when(accountService.getAccountByUsername(username)).thenReturn(account);
        when(repository.save(any(StormpathUserMapping.class))).thenReturn(null);

        handler.onAuthenticationSuccess(null, null, authentication);

        verify(authentication).getName();
        verify(repository).findByUsernameIgnoreCase(username);
        verify(accountService).getAccountByUsername(username);
        verify(repository).save(any(StormpathUserMapping.class));
    }
}