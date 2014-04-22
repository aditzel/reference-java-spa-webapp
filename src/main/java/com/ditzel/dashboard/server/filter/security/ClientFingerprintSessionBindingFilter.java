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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.AccessControlException;

/**
 * Filter responsible for establishing a "fingerprint" for the client communicating with the server and binding it to the
 * user's session. If the fingerprint changes for the given session then reject the call and prevent further calls down the
 * chain. Currently takes into account account:
 *
 * <ul>
 *     <li>The User-Agent Header</li>
 *     <li>The client's IP address</li>
 * </ul>
 *
 * The above considerations have the ramification that may be unwanted. E.g. if the user is on a mobile device such as a
 * tablet which has both WiFi as well as cellular connectivity, if the device roams between one of those networks to the
 * other then the app will reject the roaming call and the user will need to re-authenticate.
 *
 * @author Allan Ditzel
 * @since 1.0
 */
public class ClientFingerprintSessionBindingFilter extends OncePerRequestFilter {

    @Autowired
    private HttpClientFingerprintHasher httpClientFingerprintHasher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (session != null) {
            String clientFingerPrint = httpClientFingerprintHasher.fingerprintClient(request);
            String existingFingerprint = (String) session.getAttribute(Constants.USER_AGENT_FINGERPRINT);

            if (existingFingerprint == null) {
                session.setAttribute(Constants.USER_AGENT_FINGERPRINT, clientFingerPrint);
            } else {
                if (!existingFingerprint.equals(clientFingerPrint)) {
                    throw new AccessControlException("Client fingerprint doesn't match stored fingerprint.");
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
