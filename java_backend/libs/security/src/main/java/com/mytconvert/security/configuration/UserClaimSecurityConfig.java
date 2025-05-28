package com.mytconvert.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mytconvert.security.filter.UserClaimsAuthenticationFilter;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class UserClaimSecurityConfig extends AbstractSecurityConfig {

    @Bean
    @Override
    public OncePerRequestFilter authenticationFilter() {
        return new UserClaimsAuthenticationFilter();
    }
}