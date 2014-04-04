package com.ditzel.dashboard.server.filter.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Binds a {@link org.springframework.security.web.csrf.CsrfToken} to the {@link javax.servlet.http.HttpServletResponse}
 * headers if the Spring {@link org.springframework.security.web.csrf.CsrfFilter} has placed one in the {@link javax.servlet.http.HttpServletRequest}.
 */
public class CsrfTokenRequestBindingFilter extends OncePerRequestFilter {
    private static final String CSRF_TOKEN_REQUEST_KEY = "_csrf";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession httpSession = request.getSession(false);
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CSRF_TOKEN_REQUEST_KEY);

        if (csrfToken != null && httpSession != null) {
            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }

        filterChain.doFilter(request, response);
    }
}
