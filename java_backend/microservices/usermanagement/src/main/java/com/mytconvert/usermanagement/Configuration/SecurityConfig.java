package com.mytconvert.usermanagement.Configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Value("${jwt.secret_key}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita a proteção CSRF
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers(new AntPathRequestMatcher("/auth/login")).permitAll(); // Permite requisições apenas para rotas com prefixo /auth

                    auth.requestMatchers(new AntPathRequestMatcher("/users/createUser", "POST")).permitAll(); 

                    auth.requestMatchers(new AntPathRequestMatcher("/users/users/createUser", "POST")).permitAll();

                    auth.anyRequest().authenticated(); // Exige JWT para requisições para outras rotas
                })
                .sessionManagement(sM -> {
                    sM.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .oauth2ResourceServer(conf -> {
                    conf.jwt(Customizer.withDefaults()); // Configura o JWT para usar o servidor de recurso do OAuth2
                });

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        return authenticationManager;
    }

    // Encoder do JWT
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey originalKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(originalKey).build();
        return decoder;
    }

    // Decoder do JWT
    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<SecurityContext>(key);
        JwtEncoder encoder = new NimbusJwtEncoder(immutableSecret);
        return encoder;
    }

    // Encoder para hash da senha
    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder;
    }
}