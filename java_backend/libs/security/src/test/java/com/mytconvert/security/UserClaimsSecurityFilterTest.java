package com.mytconvert.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.mytconvert.security.annotation.EnableUserClaimsSecurity;
import com.mytconvert.security.entity.LoggedUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytconvert.security.UserClaimsSecurityFilterTest.TestController2;


@WebMvcTest({TestController2.class})
@EnableUserClaimsSecurity
public class UserClaimsSecurityFilterTest {

    @Autowired
    private MockMvc mockMvc;

    private LoggedUser user;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() throws JsonProcessingException, IllegalArgumentException, JWTCreationException {
        user = new LoggedUser(
            1L,
            "John Doe", 
            "john@doe.com", 
            "ADMIN"
        );
    }

    @Test
    public void testFilterWithValidToken() throws Exception {
        mockMvc.perform(get("/protected-resource")
                .header("x-user", objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Access granted"));
    }

    @Test
    public void testFilterWithInvalidToken() throws Exception {
        mockMvc.perform(get("/protected-resource")
                .header("x-user", "invalid.user"))
                .andExpect(status().isUnauthorized());
    }

    // @Test
    public void testFilterWithoutToken() throws Exception {
        mockMvc.perform(get("/protected-resource"))
                .andExpect(status().isUnauthorized());
    }

    // Classe de configuração
    @Configuration
    public static class TestConfig2 {

        @Bean
        public TestController2 testController() {
            return new TestController2();
        }
    }

    // Controlador Fictício
    @RestController
    public static class TestController2 {

        @GetMapping("/protected-resource")
        public ResponseEntity<?> protectedResource() {
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Access granted"));
        }
    }
}