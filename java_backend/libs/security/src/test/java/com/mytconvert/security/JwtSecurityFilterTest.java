package com.mytconvert.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.mytconvert.security.JwtSecurityFilterTest.TestController;
import com.mytconvert.security.annotation.EnableJwtSecurity;
import com.mytconvert.security.entity.LoggedUser;
import com.mytconvert.security.service.JwtTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import static org.hamcrest.Matchers.*;

@WebMvcTest({TestController.class})
@EnableJwtSecurity
public class JwtSecurityFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    private String token;

    @BeforeEach
    public void setup() throws JsonProcessingException, IllegalArgumentException, JWTCreationException {
        LoggedUser user = new LoggedUser(
            1L,
            "John Doe",
            "john@doe.com",
            "ADMIN"
        );
        token = jwtTokenService.generateToken(user);
    }

    @Test
    public void testFilterWithValidToken() throws Exception {
        mockMvc.perform(get("/protected-resource")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUser() throws Exception {
        String payload = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.com\",\"password\": \"Password123*\" }";

        mockMvc.perform(post("/users/createUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Usuario cadastrado")));
    }

    @Configuration
    public static class TestConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    public static class TestController {

        @GetMapping("/protected-resource")
        public ResponseEntity<?> protectedResource() {
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Acesso concedido"));
        }

        @PostMapping("/users/createUser")
        public ResponseEntity<?> createUser() {
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Usuario cadastrado com sucesso"));
        }
    }
}