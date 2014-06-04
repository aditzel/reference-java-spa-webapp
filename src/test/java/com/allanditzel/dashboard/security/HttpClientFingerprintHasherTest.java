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

package com.allanditzel.dashboard.security;

import com.allanditzel.dashboard.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for the {@link com.allanditzel.dashboard.security.HttpClientFingerprintHasher} class.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpClientFingerprintHasherTest {
    private HttpClientFingerprintHasher hasher;

    @Before
    public void setUp() {
        hasher = new HttpClientFingerprintHasher();
    }

    @Mock
    HttpServletRequest request;

   @Test(expected = IllegalArgumentException.class)
    public void failsWithNullRequestResponse() {
        hasher.fingerprintClient(null);
    }

    @Test
    public void handlesRequestsWithNoUserAgent() {
        when(request.getHeader(Constants.USER_AGENT_HEADER)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(null);

        assertNull(hasher.fingerprintClient(request));

        verify(request).getHeader(Constants.USER_AGENT_HEADER);
        verify(request).getRemoteAddr();
    }

    @Test
    public void generatesFingerprint() {
        String userAgent = "User-Agent";
        String remoteAddress = "Remote-Address";
        String expectedHashedFingerprint = DigestUtils.md5Hex(userAgent + HttpClientFingerprintHasher.DELIMETER + remoteAddress);

        when(request.getHeader(Constants.USER_AGENT_HEADER)).thenReturn(userAgent);
        when(request.getRemoteAddr()).thenReturn(remoteAddress);

        String computedHashedFingerprint = hasher.fingerprintClient(request);

        assertNotNull(computedHashedFingerprint);
        assertEquals(expectedHashedFingerprint, computedHashedFingerprint);

        verify(request).getHeader(Constants.USER_AGENT_HEADER);
        verify(request).getRemoteAddr();
    }
}
