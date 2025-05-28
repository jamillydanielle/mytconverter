package com.mytconvert.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.mytconvert.security.JwtSecurityFilterTest.TestController;
import com.mytconvert.security.annotation.EnableJwtSecurity;
import com.mytconvert.security.entity.LoggedUser;
import com.mytconvert.security.service.JwtTokenService;
import com.fasterxml.jackson.core.JsonProcessingException;

@WebMvcTest({TestController.class})
@EnableJwtSecurity
public class JwtSecurityFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @BeforeEach
    public void setup() throws JsonProcessingException, IllegalArgumentException, JWTCreationException {
        jwtTokenService.generateToken(new LoggedUser(
            1L,
            "John Doe", 
            "john@doe.com", 
            "ADMIN"
        ));
    }


    
    // @Test
    public void testFilterWithoutToken() throws Exception {
        mockMvc.perform(get("/protected-resource"))
                .andExpect(status().isUnauthorized());
    }

    // Classe de configuração
    @Configuration
    public static class TestConfig {

        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    // Controlador Fictício
    @RestController
    public static class TestController {

        @GetMapping("/protected-resource")
        public ResponseEntity<?> protectedResource() {
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Access granted"));
        }
    }
}