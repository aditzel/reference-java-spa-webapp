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

package com.allanditzel.dashboard.server.config;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;
import com.allanditzel.dashboard.server.Constants;
import com.allanditzel.dashboard.server.security.HttpClientFingerprintHasher;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.security.client.ClientFactory;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private Environment env;

    @Value("${stormpath.application.url}")
    private String stormpathApplicationUrl;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(stormpathAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CsrfTokenResponseHeaderBindingFilter csrfFilter = csrfTokenResponseHeaderBindingFilter();

        http
                .addFilterAfter(csrfFilter, CsrfFilter.class)
                .headers()
                    .cacheControl()
                    .xssProtection()
                    .and()
                .authorizeRequests()
                    .antMatchers("/static/bower_components/**").permitAll()
                    .antMatchers("/static/login*.js").permitAll()
                    .antMatchers("/static/login*.css").permitAll()
                    .antMatchers("/static/dashboard*.js").authenticated()
                    .antMatchers("/static/dashboard*.css").authenticated()
                    .antMatchers("/static/DashboardApp/**").authenticated()
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
    public CsrfTokenResponseHeaderBindingFilter csrfTokenResponseHeaderBindingFilter() {
         return new CsrfTokenResponseHeaderBindingFilter();
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
        stormpathAuthenticationProvider.setApplicationRestUrl(stormpathApplicationUrl);

        return stormpathAuthenticationProvider;
    }
}
