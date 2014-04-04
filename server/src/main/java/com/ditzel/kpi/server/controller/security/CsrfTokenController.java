package com.ditzel.kpi.server.controller.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Allan on 4/2/2014.
 */
@Controller
public class CsrfTokenController {
    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    @RequestMapping(value = "/security/csrf", method = RequestMethod.HEAD)
    public void getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getHeader("referer").endsWith("/index.html")) {
            response.setHeader(DEFAULT_CSRF_HEADER_NAME, "");
        }
    }
}
