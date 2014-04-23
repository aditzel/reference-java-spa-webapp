package com.ditzel.dashboard.server.controller.user;

import com.ditzel.dashboard.model.UserResourceAssembler;
import com.ditzel.dashboard.server.Constants;
import com.ditzel.dashboard.server.exception.UnknownResourceException;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Iterator;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextHolder.class)
public class UserControllerTest {
    @Mock
    private Client client;

    @Mock
    private UserResourceAssembler resourceAssembler;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Application application;

    @Mock
    private AccountList accountList;

    @Mock
    private Iterator<Account> accountIterator;

    @Mock
    private Account account;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "client", client);
        ReflectionTestUtils.setField(controller, "resourceAssembler", resourceAssembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldRedirectWhenAccessingCurrentUser() throws Exception {
        String currentlyLoggedInUserName = "currentlyLoggedInUser";
        String currentlyLoggedInUserNameServiceUrl = "/api/user/" + currentlyLoggedInUserName;

        mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentlyLoggedInUserName);

        mockMvc.perform(get("/api/user/current"))
                .andExpect(status().isMovedTemporarily())
                .andExpect(redirectedUrl(currentlyLoggedInUserNameServiceUrl))
                .andExpect(view().name("redirect:" + currentlyLoggedInUserNameServiceUrl));

        verify(securityContext).getAuthentication();
        verify(authentication).getName();
        verifyStatic();
    }

    @Test()
    public void shouldReturn404WhenUnknownUserSpecified() throws Exception {
        when(client.getResource(Constants.STORMPATH_APPLICATION_URL, Application.class)).thenReturn(application);
        when(application.getAccounts(any(AccountCriteria.class))).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(false);

        mockMvc.perform(get("/api/user/doesnotexist"))
                .andExpect(status().isNotFound());

        verify(client).getResource(Constants.STORMPATH_APPLICATION_URL, Application.class);
        verify(application).getAccounts(any(AccountCriteria.class));
        verify(accountList).iterator();
        verify(accountIterator).hasNext();
    }

    @Test
    public void shouldReturnUserJsonWhenKnownUserSpecified() throws Exception {
        String username = "userthatexists";

        when(client.getResource(Constants.STORMPATH_APPLICATION_URL, Application.class)).thenReturn(application);
        when(application.getAccounts(any(AccountCriteria.class))).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(true);
        when(accountIterator.next()).thenReturn(account);
        when(account.getUsername()).thenReturn(username);

        mockMvc.perform(get("/api/user/" + username).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(client).getResource(Constants.STORMPATH_APPLICATION_URL, Application.class);
        verify(application).getAccounts(any(AccountCriteria.class));
        verify(accountList).iterator();
        verify(accountIterator).hasNext();
    }
}