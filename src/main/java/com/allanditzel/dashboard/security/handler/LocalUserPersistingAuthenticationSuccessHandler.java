package com.allanditzel.dashboard.security.handler;

import com.allanditzel.dashboard.exception.ApplicationException;
import com.allanditzel.dashboard.exception.UnknownResourceException;
import com.allanditzel.dashboard.model.StormpathUserMapping;
import com.allanditzel.dashboard.persistence.StormpathUserMappingRepository;
import com.allanditzel.dashboard.service.account.AccountService;
import com.allanditzel.dashboard.service.user.UserService;
import com.stormpath.sdk.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler responsible for persisting the stormpath related information for the current user if it's
 * not already present. This ensures that the application can handle users that are created by
 * hand on the Stormpath service.
 */
@Component
public class LocalUserPersistingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(LocalUserPersistingAuthenticationSuccessHandler.class);

    @Autowired
    private StormpathUserMappingRepository stormpathUserMappingRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        StormpathUserMapping mapping = stormpathUserMappingRepository.findByUsernameIgnoreCase(username);

        if (mapping == null)  {
            Account account = accountService.getAccountByUsername(username);
            if (account == null) {
                throw new ApplicationException("Could not find account in Stormpath.");
            }
            mapping = new StormpathUserMapping(username, account.getHref());
            stormpathUserMappingRepository.save(mapping);
        }
    }
}
