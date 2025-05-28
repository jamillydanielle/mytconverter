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
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String INVALID_CREDENTIALS = "The username or password is incorrect. Please try again.";
    private static final String ACCOUNT_DISABLED = "Account disabled";
    private static final String ACCOUNT_LOCKED = "Account locked";
    private static final String CREDENTIALS_EXPIRED = "Credentials expired";
    private static final String INTERNAL_ERROR = "Internal server error";

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.issuer}")
    private String issuer;

    @Autowired
    private UserRepository userRepository; // Inject repository to persist test data

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserService userService;

    private User user;

    private ObjectMapper objectMapper = new ObjectMapper();

    private LoginRequest loginRequest;

    private String payloadCreateTestUser = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.br\", \"type\": \"USER\" \"\" }";

    @BeforeEach
    public void setUp() {
        // Clear the database before each test to ensure a clean state
        userRepository.deleteAll();

        // Hashes the password
        String password = encoder.encode("1234");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@example.br");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        // Create a test user and save it in the database
        user = new User(
                "John Doe",
                "john.doe@example.br",
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
                "john1.doe@example.br",
                password,
                UserType.ADMIN);

        userRepository.save(testUser);

        // Scenario 1: Valid password
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setNewPassword("New@Secure123");

        mockMvc.perform(
                put("/auth/change-password/{email}", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully."));

        // Scenario 2: User does not exist
        mockMvc.perform(
                put("/auth/change-password/nonexistent@example.br")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));

        // Scenario 3: Empty password
        changePasswordRequest.setNewPassword("");
        mockMvc.perform(
                put("/auth/change-password/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The required field 'newPassword' is empty."));

        // Scenario 4: Weak password
        changePasswordRequest.setNewPassword("weak");
        mockMvc.perform(
                put("/auth/change-password/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        "The provided password is too weak. Please ensure it includes at least one number, one uppercase letter, one lowercase letter, and have length between 8 and 16 characters."));
    }

    // Testa o login e verifica se o token foi gerado
    @Test
    public void testLogin() throws Exception {

        loginRequest.setEmail("john.doe@example.br");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Verifica se o status Ã© 200
                .andExpect(content().string(not(emptyOrNullString()))); // Verifica se o token estÃ¡ presente no JSON de
                                                                        // resposta
    }

    // Teste que verifica se o issuer do token Ã© vÃ¡lido
    @Test
    public void testTokenValidIssuer() throws Exception {

        loginRequest.setEmail("john.doe@example.br");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Verifica se o status eh 200
                .andExpect(content().string(not(emptyOrNullString()))) // Verifica se o token estÃ¡ presente no JSON de
                                                                       // resposta
                .andReturn().getResponse().getContentAsString(); // ObtÃ©m o token

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];

        String tokenIssuer = getTokenIssuer(token);
        assertEquals(issuer, tokenIssuer);
    }

    // Teste que verifica se o subject do token Ã© vÃ¡lido
    @Test
    public void testTokenValidSubject() throws Exception {

        loginRequest.setEmail("john.doe@example.br");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Verifica se o status Ã© 200
                .andExpect(content().string(not(emptyOrNullString()))) // Verifica se o token estÃ¡ presente no JSON de
                                                                       // resposta
                .andReturn().getResponse().getContentAsString(); // ObtÃ©m o token

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];
        String tokenSubject = getTokenSubject(token);
        assertEquals(loginRequest.getEmail(), tokenSubject);
    }

    // Teste que verifica se o token nÃ£o estÃ¡ expirado
    @Test
    public void testTokenNotExpired() throws Exception {

        loginRequest.setEmail("john.doe@example.br");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Verifica se o status Ã© 200
                .andExpect(content().string(not(emptyOrNullString()))) // Verifica se o token estÃ¡ presente no JSON de
                                                                       // resposta
                .andReturn().getResponse().getContentAsString(); // ObtÃ©m o token

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];
        Date tokenExpiresAt = getTokenExpiration(token);
        assertTrue(tokenExpiresAt.after(new Date()));
    }

    // Teste que verifica se o token estÃ¡ expirado
    @Test
    public void testTokenExpired() throws Exception {

        loginRequest.setEmail("john.doe@example.br");
        loginRequest.setPassword("1234");
        loginRequest.setRememberMe(false);

        String jsonResponse = mockMvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Verifica se o status Ã© 200
                .andExpect(content().string(not(emptyOrNullString()))) // Verifica se o token estÃ¡ presente no JSON de
                                                                       // resposta
                .andReturn().getResponse().getContentAsString(); // ObtÃ©m o token

        String token = jsonResponse.split("\"token\":\"")[1].split("\"")[0];
        Date tokenExpiresAt = getTokenExpiration(token);
        LocalDateTime mockDate = LocalDateTime.of(2026, 10, 15, 10, 0); // Data futura mockada

        assertFalse(tokenExpiresAt.after(Date.from(mockDate.atZone(ZoneId.systemDefault()).toInstant()))); // ComparaÃ§Ã£o
                                                                                                           // da data do
                                                                                                           // token com
                                                                                                           // a data
                                                                                                           // futura
                                                                                                           // mockada
    }

    @Test
    public void testLoginUserNotFound() throws Exception {

        loginRequest.setEmail("not_found@example.br");
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