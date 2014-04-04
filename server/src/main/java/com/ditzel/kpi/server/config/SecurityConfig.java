package com.ditzel.kpi.server.config;

import com.ditzel.kpi.server.filter.security.CsrfTokenRequestBindingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;

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
        http
                .addFilterAfter(new CsrfTokenRequestBindingFilter(), CsrfFilter.class)
                .authorizeRequests()
                    .antMatchers("/assets/**").permitAll()
                    .antMatchers("/security/csrf").permitAll()
                    .antMatchers("/*.html").authenticated()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/index.html")
                    .permitAll();
    }
}
