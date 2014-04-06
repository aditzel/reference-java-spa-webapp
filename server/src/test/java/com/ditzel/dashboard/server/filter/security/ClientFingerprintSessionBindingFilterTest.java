package com.ditzel.dashboard.server.filter.security;

import com.ditzel.dashboard.server.Constants;
import com.ditzel.dashboard.server.security.HttpClientFingerprintHasher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.security.AccessControlException;

import static org.mockito.Mockito.*;

/**
 * Test for {@link com.ditzel.dashboard.server.filter.security.ClientFingerprintSessionBindingFilter}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientFingerprintSessionBindingFilterTest {
    private ClientFingerprintSessionBindingFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpSession session;

    @Mock
    private HttpClientFingerprintHasher httpClientFingerprintHasher;

    @Before
    public void setUp() {
        filter = new ClientFingerprintSessionBindingFilter();
        ReflectionTestUtils.setField(filter, "httpClientFingerprintHasher", httpClientFingerprintHasher);
    }

    @Test
    public void doNothingIfNoSessionFound() throws ServletException, IOException {
        when(request.getSession(false)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verifyZeroInteractions(httpClientFingerprintHasher, response);
        verify(request).getSession(false);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(request);
    }

    @Test
    public void setFingerprintIfNotFoundInSession() throws ServletException, IOException {
        String fingerprint = "fingerprint";

        when(request.getSession(false)).thenReturn(session);
        when(httpClientFingerprintHasher.fingerprintClient(request)).thenReturn(fingerprint);
        when(session.getAttribute(Constants.USER_AGENT_FINGERPRINT)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).getSession(false);
        verify(session).getAttribute(Constants.USER_AGENT_FINGERPRINT);
        verify(session).setAttribute(Constants.USER_AGENT_FINGERPRINT, fingerprint);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(request);
    }

    @Test(expected = AccessControlException.class)
    public void throwExceptionIfClientFingerprintDoesntMatchStoredFingerprint() throws ServletException, IOException {
        String fingerprint = "fingerprint";
        String storedFingerprint = "storedFingerprint";

        when(request.getSession(false)).thenReturn(session);
        when(httpClientFingerprintHasher.fingerprintClient(request)).thenReturn(fingerprint);
        when(session.getAttribute(Constants.USER_AGENT_FINGERPRINT)).thenReturn(storedFingerprint);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).getSession(false);
        verify(session).getAttribute(Constants.USER_AGENT_FINGERPRINT);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(request, session);
    }
}
