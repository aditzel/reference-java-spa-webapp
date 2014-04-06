package com.ditzel.dashboard.server.security;

import com.ditzel.dashboard.server.Constants;
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
 * Created by Allan on 4/5/2014.
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
        when(request.getHeader(Constants.USER_AGENT_HEADER_NAME)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(null);

        assertNull(hasher.fingerprintClient(request));

        verify(request).getHeader(Constants.USER_AGENT_HEADER_NAME);
        verify(request).getRemoteAddr();
    }

    @Test
    public void generatesFingerprint() {
        String userAgent = "User-Agent";
        String remoteAddress = "Remote-Address";
        String expectedHashedFingerprint = DigestUtils.md5Hex(userAgent + HttpClientFingerprintHasher.DELIMETER + remoteAddress);

        when(request.getHeader(Constants.USER_AGENT_HEADER_NAME)).thenReturn(userAgent);
        when(request.getRemoteAddr()).thenReturn(remoteAddress);

        String computedHashedFingerprint = hasher.fingerprintClient(request);

        assertNotNull(computedHashedFingerprint);
        assertEquals(expectedHashedFingerprint, computedHashedFingerprint);

        verify(request).getHeader(Constants.USER_AGENT_HEADER_NAME);
        verify(request).getRemoteAddr();
    }
}
