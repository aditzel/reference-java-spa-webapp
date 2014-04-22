package com.ditzel.dashboard.server.config;

import com.ditzel.dashboard.server.Constants;
import com.ditzel.dashboard.server.filter.security.ClientFingerprintSessionBindingFilter;
import com.ditzel.dashboard.server.filter.security.CsrfTokenRequestBindingFilter;
import com.ditzel.dashboard.server.security.HttpClientFingerprintHasher;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.security.client.ClientFactory;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CsrfFilter;

/**
 * Security related configuration class
 *
 * @author Allan Ditzel
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    Environment env;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(stormpathAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CsrfTokenRequestBindingFilter csrfTokenRequestBindingFilter = csrfTokenRequestBindingFilter();
        ClientFingerprintSessionBindingFilter clientFingerprintSessionBindingFilter = clientFingerprintSessionBindingFilter();

        http
                .addFilterAfter(clientFingerprintSessionBindingFilter, CsrfFilter.class)
                .addFilterAfter(csrfTokenRequestBindingFilter, ClientFingerprintSessionBindingFilter.class)
                .headers()
                    .cacheControl()
                    .xssProtection()
                    .and()
                .authorizeRequests()
                    .antMatchers("/assets/bootstrap/**").permitAll()
                    .antMatchers("/assets/css/**").permitAll()
                    .antMatchers("/assets/js/angular/**").permitAll()
                    .antMatchers("/assets/js/jquery/**").permitAll()
                    .antMatchers("/assets/js/LoginApp/**").permitAll()
                    .antMatchers("/assets/js/DashboardApp/**").authenticated()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/index.html")
                    .defaultSuccessUrl("/home.html", true)
                    .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                    .permitAll()
                    .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/index.html")
                    .invalidateHttpSession(true)
                    .and()
                .requiresChannel()
                    .anyRequest().requiresSecure();
    }

    @Bean
    public CsrfTokenRequestBindingFilter csrfTokenRequestBindingFilter() {
         return new CsrfTokenRequestBindingFilter();
    }

    @Bean
    public ClientFingerprintSessionBindingFilter clientFingerprintSessionBindingFilter() {
        return new ClientFingerprintSessionBindingFilter();
    }

    @Bean
    public HttpClientFingerprintHasher httpClientFingerprintHasher() {
        return new HttpClientFingerprintHasher();
    }

    @Bean
    public Client stormpathClient() throws Exception {
        ClientFactory clientFactory = new ClientFactory();

        clientFactory.setApiKeyFileLocation(Constants.STORMPATH_API_KEY_LOCATION);

        return clientFactory.getClientBuilder().build();
    }

    @Bean
    public StormpathAuthenticationProvider stormpathAuthenticationProvider() throws Exception {
        StormpathAuthenticationProvider stormpathAuthenticationProvider = new StormpathAuthenticationProvider();

        Client client = stormpathClient();
        stormpathAuthenticationProvider.setClient(client);
        stormpathAuthenticationProvider.setApplicationRestUrl(Constants.STORMPATH_APPLICATION_URL);

        return stormpathAuthenticationProvider;
    }
}
