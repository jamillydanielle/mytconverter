package com.mytconvert.datamanagement.Configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.mytconvert.datamanagement.filter.UserActiveCheckFilter;

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
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    private final UserActiveCheckFilter userActiveCheckFilter;

    @Value("${jwt.secret_key}")
    private String secretKey;

    public SecurityConfig(UserActiveCheckFilter userActiveCheckFilter) {
        this.userActiveCheckFilter = userActiveCheckFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita a proteção CSRF
                .authorizeHttpRequests(auth -> {

                    auth.requestMatchers(new AntPathRequestMatcher("/auth/login")).permitAll(); // Permite requisições apenas para rotas com prefixo /auth
                    
                    // Endpoints de recuperação de senha
                    auth.requestMatchers(new AntPathRequestMatcher("/auth/passwordReset/**")).permitAll();

                    auth.requestMatchers(new AntPathRequestMatcher("/users/createUser", "POST")).permitAll(); 

                    auth.requestMatchers(new AntPathRequestMatcher("/users/users/createUser", "POST")).permitAll();
                    
                    // Permitir explicitamente o método PUT para ativação de conta
                    auth.requestMatchers(new AntPathRequestMatcher("/users/activate", "PUT")).permitAll();
                    auth.requestMatchers(new AntPathRequestMatcher("/users/users/activate", "PUT")).permitAll();

                    // TEMPORARY: Permit all access to conversions endpoints for testing
                    auth.requestMatchers(new AntPathRequestMatcher("/conversions/**")).permitAll();
                    
                    // Allow test endpoints in test environment
                    if (isTestEnvironment()) {
                        auth.requestMatchers(new AntPathRequestMatcher("/conversions/**")).permitAll();
                        auth.requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll();
                    }

                    auth.anyRequest().authenticated(); // Exige JWT para requisições para outras rotas
                })
                .sessionManagement(sM -> {
                    sM.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .oauth2ResourceServer(conf -> {
                    conf.jwt(Customizer.withDefaults()); // Configura o JWT para usar o servidor de recurso do OAuth2
                })
                // Adiciona o filtro de verificação de usuário ativo após o filtro de autenticação
                .addFilterAfter(userActiveCheckFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private boolean isTestEnvironment() {
        // Check if we're running in a test environment
        return System.getProperty("spring.profiles.active", "").contains("test") || 
               System.getenv("SPRING_PROFILES_ACTIVE") != null && 
               System.getenv("SPRING_PROFILES_ACTIVE").contains("test");
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