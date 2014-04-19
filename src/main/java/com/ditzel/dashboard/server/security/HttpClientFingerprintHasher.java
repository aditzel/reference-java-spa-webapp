package com.ditzel.dashboard.server.security;

import com.ditzel.dashboard.server.Constants;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Allan on 4/5/2014.
 */
public class HttpClientFingerprintHasher {
    protected static final String DELIMETER = "   ";

    public String fingerprintClient(HttpServletRequest request) {
        String fingerPrint = null;

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        String userAgent = request.getHeader(Constants.USER_AGENT_HEADER);
        String remoteAddress = request.getRemoteAddr();

        if (userAgent != null && !userAgent.isEmpty() && remoteAddress != null && !remoteAddress.isEmpty()) {
            fingerPrint = DigestUtils.md5Hex(userAgent + DELIMETER + remoteAddress);
        }

        return fingerPrint;
    }
}
