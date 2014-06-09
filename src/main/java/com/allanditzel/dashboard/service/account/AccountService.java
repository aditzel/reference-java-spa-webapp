package com.allanditzel.dashboard.service.account;

import com.allanditzel.dashboard.model.User;
import com.stormpath.sdk.account.Account;

/**
 * Service for {@link com.stormpath.sdk.account.Account} specific operations.
 *
 * @since 1.0
 */
public interface AccountService {
    Account getAccountByUsername(String username);

    Account getAccountByUrl(String url);

    Account createAccountFromUser(User user);
}
