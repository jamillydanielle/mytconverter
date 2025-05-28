package com.mytconvert.usermanagement.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.mytconvert.usermanagement.security.UserAuthenticated;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    private static final long DEFAULT_EXPIRY_DURATION_HOURS = 1L;
    private static final long LONGER_EXPIRY_DURATION_DAYS = 7L;

    public String generateToken(UserAuthenticated userAuthenticated, boolean isRememberMe) throws JWTCreationException, JsonProcessingException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String scopes = userAuthenticated.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(" "));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(Date.from(creationDate()))
                .withExpiresAt(Date.from(expirationDate(isRememberMe)))
                .withSubject(userAuthenticated.getUsername())
                .withClaim("scope", scopes)
                .withClaim("user", objectMapper.writeValueAsString(userAuthenticated))
                .sign(algorithm);
    }

    private Instant creationDate() {
        return ZonedDateTime.now(ZoneId.systemDefault()).toInstant();
    }

    private Instant expirationDate(boolean isRememberMe) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        if (isRememberMe) {
            return now.plusDays(LONGER_EXPIRY_DURATION_DAYS).toInstant();
        } else {
            return now.plusHours(DEFAULT_EXPIRY_DURATION_HOURS).toInstant();
        }
    }

    // Método para verificar e extrair informações do token, se necessário
    public UserAuthenticated getUserFromToken(String token) throws JsonProcessingException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String userJson = JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token)
                .getClaim("user")
                .asString();

        return new ObjectMapper().readValue(userJson, UserAuthenticated.class);
    }
}