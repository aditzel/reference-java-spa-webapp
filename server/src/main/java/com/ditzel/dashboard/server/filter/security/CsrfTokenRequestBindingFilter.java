package com.ditzel.dashboard.server.filter.security;

import com.ditzel.dashboard.server.Constants;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession();
        CsrfToken requestCsrfToken = (CsrfToken) request.getAttribute(Constants.CSRF_TOKEN_KEY);
        CsrfToken sessionCsrfToken = (CsrfToken) session.getAttribute(Constants.CSRF_TOKEN_KEY);

        if (requestCsrfToken == null && sessionCsrfToken != null) {
            requestCsrfToken = sessionCsrfToken;
        } else if (requestCsrfToken != null && sessionCsrfToken == null) {
            session.setAttribute(Constants.CSRF_TOKEN_KEY, requestCsrfToken);
        }

        if (requestCsrfToken != null) {
            response.setHeader(requestCsrfToken.getHeaderName(), requestCsrfToken.getToken());
        }

        filterChain.doFilter(request, response);
    }
}
