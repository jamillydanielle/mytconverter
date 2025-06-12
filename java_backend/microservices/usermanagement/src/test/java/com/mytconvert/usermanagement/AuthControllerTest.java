package com.mytconvert.usermanagement;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mytconvert.usermanagement.dto.ChangePasswordRequest;
import com.mytconvert.usermanagement.dto.LoginRequest;
import com.mytconvert.usermanagement.entity.User;
import com.mytconvert.usermanagement.entity.UserType;
import com.mytconvert.usermanagement.repository.UserRepository;
import com.mytconvert.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private static final String USER_NOT_FOUND = "Usuario nao encontrado";
    private static final String INVALID_CREDENTIALS = "Email ou senha estao incorretos. Tente novamente.";
    private static final String ACCOUNT_DISABLED = "Conta desativada";
    private static final String ACCOUNT_LOCKED = "Conta travada";
    private static final String CREDENTIALS_EXPIRED = "Credenciais expiradas";
    private static final String INTERNAL_ERROR = "Internal server error";

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.issuer}")
    private String issuer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserService userService;

    private User user;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LoginRequest loginRequest;

    private String payloadCreateTestUser = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.com\", \"type\": \"USER\" \"\" }";

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        String password = encoder.encode("1234");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        user = new User(
                "John Doe",
                "john.doe@example.com",
                password,
                UserType.ADMIN);
        userRepository.save(user);
    }

    private String getTokenIssuer(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getIssuer();
    }

    private String getTokenSubject(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }

    private Date getTokenExpiration(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getExpiresAt();
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testChangePassword() throws Exception {
        String password = encoder.encode("1234");
        User testUser = new User(
                "John Doe",
                "john1.doe@example.com",
                password,
                UserType.ADMIN);

        userRepository.save(testUser);

        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setNewPassword("New@Secure123");

        mockMvc.perform(
                put("/auth/change-password/{email}", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Senha atualizada com sucesso."));

        mockMvc.perform(
                put("/auth/change-password/nonexistent@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));

        changePasswordRequest.setNewPassword("");
        mockMvc.perform(
                put("/auth/change-password/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O campo obrigatorio 'newPassword' esta vazio."));

        changePasswordRequest.setNewPassword("weak");
        mockMvc.perform(
                put("/auth/change-password/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "A senha fornecida e muito fraca. Por favor garanta que a senha incua pelo menos um numero, uma letra maiuscula, uma letra minuscula e tenha entre 8 e 16 caracteres."));
    }

    @Test
    public void testLogin() throws Exception {

        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyOrNullString())));
    }

    @Test
    public void testTokenValidIssuer() throws Exception {

        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyOrNullString())))
                .andReturn().getResponse().getContentAsString();

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];

        String tokenIssuer = getTokenIssuer(token);
        assertEquals(issuer, tokenIssuer);
    }

    @Test
    public void testTokenValidSubject() throws Exception {

        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyOrNullString())))
                .andReturn().getResponse().getContentAsString();

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];
        String tokenSubject = getTokenSubject(token);
        assertEquals(loginRequest.getEmail(), tokenSubject);
    }

    @Test
    public void testTokenNotExpired() throws Exception {

        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) 
                .andExpect(content().string(not(emptyOrNullString()))) 
                .andReturn().getResponse().getContentAsString();

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];
        Date tokenExpiresAt = getTokenExpiration(token);
        assertTrue(tokenExpiresAt.after(new Date()));
    }

    @Test
    public void testTokenExpired() throws Exception {

        loginRequest.setEmail("john.doe@example.com");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) 
                .andExpect(content().string(not(emptyOrNullString()))) 
                .andReturn().getResponse().getContentAsString(); 

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];
        Date tokenExpiresAt = getTokenExpiration(token);
        LocalDateTime mockDate = LocalDateTime.of(2026, 10, 15, 10, 0);

        assertFalse(tokenExpiresAt.after(Date.from(mockDate.atZone(ZoneId.systemDefault()).toInstant()))); 
    }

    @Test
    public void testLoginUserNotFound() throws Exception {

        loginRequest.setEmail("not_found@example.com");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);


        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(USER_NOT_FOUND));
    }
}