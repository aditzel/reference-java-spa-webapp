package com.allanditzel.dashboard.server.controller.user;

import com.allanditzel.dashboard.model.resource.UserResource;
import com.allanditzel.dashboard.model.resource.UserResourceAssembler;
import com.allanditzel.dashboard.server.exception.ApplicationException;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupCriteria;
import com.stormpath.sdk.group.GroupList;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.Charset;
import java.util.Iterator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class, RandomStringUtils.class})
public class UserControllerTest {
    private static final String STORMPATH_APPLICATION_URL = "stormpathUrl";

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

    private UserController userController;

    private String jsonPayload = "{\"username\":\"testUsername\",\"firstName\":\"testFirstName\",\"lastName\":\"testLastName\", \"email\":\"testEmail@domain.com\", \"roles\":[\"user\"]}";

    private MediaType application_json_utf8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Before
    public void setUp() {
        userController = new UserController();
        ReflectionTestUtils.setField(userController, "client", client);
        ReflectionTestUtils.setField(userController, "resourceAssembler", resourceAssembler);
        ReflectionTestUtils.setField(userController, "stormpathApplicationUrl", STORMPATH_APPLICATION_URL);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
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
        when(client.getResource(STORMPATH_APPLICATION_URL, Application.class)).thenReturn(application);
        when(application.getAccounts(any(AccountCriteria.class))).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(false);

        mockMvc.perform(get("/api/user/doesnotexist"))
                .andExpect(status().isNotFound());

        verify(client).getResource(STORMPATH_APPLICATION_URL, Application.class);
        verify(application).getAccounts(any(AccountCriteria.class));
        verify(accountList).iterator();
        verify(accountIterator).hasNext();
    }

    @Test
    public void shouldReturnUserJsonWhenKnownUserSpecified() throws Exception {
        String username = "userthatexists";

        when(client.getResource(STORMPATH_APPLICATION_URL, Application.class)).thenReturn(application);
        when(application.getAccounts(any(AccountCriteria.class))).thenReturn(accountList);
        when(accountList.iterator()).thenReturn(accountIterator);
        when(accountIterator.hasNext()).thenReturn(true);
        when(accountIterator.next()).thenReturn(account);
        when(account.getUsername()).thenReturn(username);

        mockMvc.perform(get("/api/user/" + username).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(client).getResource(STORMPATH_APPLICATION_URL, Application.class);
        verify(application).getAccounts(any(AccountCriteria.class));
        verify(accountList).iterator();
        verify(accountIterator).hasNext();
    }

    @Test(expected = ApplicationException.class)
    public void shouldFailIfUserGroupDoesNotExist() {
        UserResource userResource = mock(UserResource.class);
        GroupList groupList = mock(GroupList.class);
        Iterator<Group> groupIterator = mock(Iterator.class);
        when(client.getResource(STORMPATH_APPLICATION_URL, Application.class)).thenReturn(application);
        when(application.getGroups(any(GroupCriteria.class))).thenReturn(groupList);
        when(groupList.iterator()).thenReturn(groupIterator);
        when(groupIterator.hasNext()).thenReturn(false);
        when(userResource.getEmail()).thenReturn("email");

        userController.createUser(userResource);

        verify(client).getResource(STORMPATH_APPLICATION_URL, Application.class);
        verify(application).getGroups(any(GroupCriteria.class));
        verify(groupList.iterator());
        verify(groupIterator).hasNext();
        verify(userResource).getEmail();
    }

    @Test
    public void ensurecreateNewAccountInstanceWithRandomPasswordCallsNecessaryFields() throws Exception {
        String randomPassword = "password";
        String username = "testUsername";
        String email = "testEmail@domain.com";
        String firstName = "testFirstName";
        String lastName = "testLastName";

        UserResource userResource = mock(UserResource.class);

        mockStatic(RandomStringUtils.class);
        GroupList groupList = mock(GroupList.class);
        Iterator<Group> groupIterator = mock(Iterator.class);
        Group group = mock(Group.class);
        when(RandomStringUtils.random(64, true, true)).thenReturn(randomPassword);
        when(client.getResource(STORMPATH_APPLICATION_URL, Application.class)).thenReturn(application);
        when(application.getGroups(any(GroupCriteria.class))).thenReturn(groupList);
        when(groupList.iterator()).thenReturn(groupIterator);
        when(groupIterator.hasNext()).thenReturn(true);
        when(groupIterator.next()).thenReturn(group);
        when(client.instantiate(Account.class)).thenReturn(account);
        when(userResource.getUsername()).thenReturn(username);
        when(userResource.getEmail()).thenReturn(email);
        when(userResource.getFirstName()).thenReturn(firstName);
        when(userResource.getLastName()).thenReturn(lastName);
        when(application.createAccount(account)).thenReturn(account);
        when(account.addGroup(group)).thenReturn(null);

        mockMvc.perform(
                post("/api/user")
                        .contentType(application_json_utf8)
                        .content(jsonPayload)
                )
                .andExpect(status().isOk());

        verify(client).getResource(STORMPATH_APPLICATION_URL, Application.class);
        verify(application).getGroups(any(GroupCriteria.class));
        verify(groupList).iterator();
        verify(groupIterator).hasNext();
        verify(groupIterator).next();
        verify(client).instantiate(Account.class);
        verify(account).setUsername(username);
        verify(account).setEmail(email);
        verify(account).setGivenName(firstName);
        verify(account).setSurname(lastName);
        verify(account).setPassword(randomPassword);
        verify(application).createAccount(account);
        verify(account).addGroup(group);
        verify(account).save();
        verifyStatic();
    }
}