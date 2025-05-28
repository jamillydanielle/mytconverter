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

    // Injetando as propriedades do application.properties
    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(UserInterface user) throws JsonProcessingException, IllegalArgumentException, JWTCreationException {
        // Define o algoritmo HMAC SHA256 para criar a assinatura do token passando a chave secreta definida
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
            .withIssuer(issuer) // Define o emissor do token
            .withIssuedAt(creationDate()) // Define a data de emissão do token
            .withExpiresAt(expirationDate()) // Define a data de expiração do token
            .withSubject(user.getEmail()) // Define o assunto do token (neste caso, o nome de usuário)
            .withClaim("user", new ObjectMapper().writeValueAsString(user))
            .sign(algorithm); // Assina o token usando o algoritmo especificado
    }

    public LoggedUser getUserFromToken(String token) throws JsonMappingException, JsonProcessingException, JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return new ObjectMapper().readValue(
            JWT.require(algorithm)
                .withIssuer(issuer) // Define o emissor do token
                .build()
                .verify(token) // Verifica a validade do token
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