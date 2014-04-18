package com.ditzel.dashboard.server.config;

import com.ditzel.dashboard.server.filter.security.ClientFingerprintSessionBindingFilter;
import com.ditzel.dashboard.server.filter.security.CsrfTokenRequestBindingFilter;
import com.ditzel.dashboard.server.security.HttpClientFingerprintHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
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
                    .antMatchers("/*.html").authenticated()
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
}
