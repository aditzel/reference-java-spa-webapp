package com.ditzel.dashboard.server.controller.user;

import com.ditzel.dashboard.model.UserResourceAssembler;
import com.stormpath.sdk.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
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
}