/*
 * Copyright 2014 Allan Ditzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 *
 * @author Allan Ditzel
 * @since 1.0
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
        when(request.getSession()).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verifyZeroInteractions(httpClientFingerprintHasher, response);
        verify(request).getSession();
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(request);
    }

    @Test
    public void setFingerprintIfNotFoundInSession() throws ServletException, IOException {
        String fingerprint = "fingerprint";

        when(request.getSession()).thenReturn(session);
        when(httpClientFingerprintHasher.fingerprintClient(request)).thenReturn(fingerprint);
        when(session.getAttribute(Constants.USER_AGENT_FINGERPRINT)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).getSession();
        verify(session).getAttribute(Constants.USER_AGENT_FINGERPRINT);
        verify(session).setAttribute(Constants.USER_AGENT_FINGERPRINT, fingerprint);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(request);
    }

    @Test(expected = AccessControlException.class)
    public void throwExceptionIfClientFingerprintDoesntMatchStoredFingerprint() throws ServletException, IOException {
        String fingerprint = "fingerprint";
        String storedFingerprint = "storedFingerprint";

        when(request.getSession()).thenReturn(session);
        when(httpClientFingerprintHasher.fingerprintClient(request)).thenReturn(fingerprint);
        when(session.getAttribute(Constants.USER_AGENT_FINGERPRINT)).thenReturn(storedFingerprint);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).getSession();
        verify(session).getAttribute(Constants.USER_AGENT_FINGERPRINT);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(request, session);
    }
}
