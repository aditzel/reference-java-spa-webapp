package com.ditzel.dashboard.server.controller.test;

import com.ditzel.dashboard.server.exception.AccessNotAllowedException;
import com.ditzel.dashboard.server.exception.test.NotAuthenticatedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that generates HTTP status codes for client testing.
 */
@Controller
@RequestMapping("/api/test")
public class StatusCodeGeneratingController {
    @RequestMapping("/generate403")
    public void generate403() {
        throw new AccessNotAllowedException();
    }

    @RequestMapping("/generate401")
    public void generate401() {
        throw new NotAuthenticatedException();
    }
}
