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

package com.ditzel.dashboard.server.security;

import com.ditzel.dashboard.server.Constants;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Creates a hash based on the <code>User-Agent</code> header and the client's remote IP address.
 *
 * @author Allan Ditzel
 * @since 1.0
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
