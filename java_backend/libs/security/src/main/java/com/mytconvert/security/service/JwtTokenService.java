package com.mytconvert.security.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mytconvert.security.entity.LoggedUser;
import com.mytconvert.security.entity.UserInterface;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class JwtTokenService {

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(UserInterface user) throws JsonProcessingException, IllegalArgumentException, JWTCreationException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
            .withIssuer(issuer)
            .withIssuedAt(creationDate())
            .withExpiresAt(expirationDate())
            .withSubject(user.getEmail())
            .withClaim("user", new ObjectMapper().writeValueAsString(user))
            .sign(algorithm);
    }

    public LoggedUser getUserFromToken(String token) throws JsonMappingException, JsonProcessingException, JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return new ObjectMapper().readValue(
            JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
                .verify(token) 
                .getClaim("user")
                .asString(),
            LoggedUser.class
        );
    }

    private Instant creationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
    }

    private Instant expirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Recife")).plusHours(4).toInstant();
    }
}