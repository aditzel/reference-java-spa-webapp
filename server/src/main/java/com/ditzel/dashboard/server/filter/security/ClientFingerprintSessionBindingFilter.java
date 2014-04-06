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
 * If a session is present will look for a fingerprint that represents the client making the request.
 */
public class ClientFingerprintSessionBindingFilter extends OncePerRequestFilter {

    @Autowired
    private HttpClientFingerprintHasher httpClientFingerprintHasher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

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
