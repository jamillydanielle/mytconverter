package com.mytconvert.usermanagement;

import com.mytconvert.usermanagement.entity.User;
import com.mytconvert.usermanagement.entity.UserType;
import com.mytconvert.usermanagement.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User("John Doe", "john@example.com", "password", UserType.USER);
        userRepository.save(user);
    }

    private User createTestUser(String name, String email, String type) {
       
        UserType userType = UserType.valueOf(type.toUpperCase());
    
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setType(userType);
        user.setPassword("encodedPassword");
    
        return userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testCheckUser() throws Exception {
        String username = "john@example.com";

        mockMvc.perform(get("/users/checkUser")
                .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testListUsers() throws Exception {
        mockMvc.perform(get("/users/list")
                .param("page", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserById() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();

        mockMvc.perform(get("/users/list/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    void testCreateUser() throws Exception {
        String payload = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.com\",\"password\": \"Password123*\" }";

        mockMvc.perform(post("/users/createUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("Usuario cadastrado")))
                .andExpect(jsonPath("$.userName", is("Jane Doe")));
    }

    @Test
    void createUser_ExistingEmail() throws Exception {
        createTestUser("Jane Doe", "jane@example.com", "USER");

        String payload = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.com\", \"password\": \"password\"}";

        mockMvc.perform(post("/users/createUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Este email pertence a outro usuario")));
    }

   

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testUpdateUser() throws Exception {
        User user = createTestUser("John Doe", "john1@example.com", "USER");

        String payload = "{ \"name\": \"John Updated\", \"email\": \"joh1n@example.com\", \"password\": \"password\"}";

        mockMvc.perform(put("/users/edit/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("User updated")))
                .andExpect(jsonPath("$.userName", is("John Updated")));
    }

    @Test
    @WithMockUser(username = "user", roles = { "ADMIN" })
    void editUser_ExistingEmail() throws Exception {
        createTestUser("Jane Doe", "jane@example.com", "USER");
        User editUser = createTestUser("Jane Doe", "jane1@example.com", "USER");

        String payload = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.com\", \"password\": \"password\"}";

        mockMvc.perform(put("/users/edit/" + editUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Este email pertence a outro usuario")));
    }
  
    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testDeactivateUser() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();

        mockMvc.perform(put("/users/" + userId + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void testActivateUser() throws Exception {
        Long userId = userRepository.findAll().get(0).getId();

        mockMvc.perform(put("/users/" + userId + "/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));

        mockMvc.perform(put("/users/" + userId + "/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void checkAllPossibleEmptyFieldsWhenCreatingOrUpdatingUsers() throws Exception {
        String name = "Jane Doe";
        String email = "jane@example.com";
        String password = "Password123*";
    

        List<String> requiredFields = Arrays.asList("name", "email", "password");
    
        for (int i = 0; i < requiredFields.size(); i++) {
            String currentName = (i == 0) ? "" : name;
            String currentEmail = (i == 1) ? "" : email;
            String currentPassword = (i == 2) ? "" : password;
    
            String payload = String.format(
                "{ \"name\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}",
                currentName,
                currentEmail,
                currentPassword
            );
    
            mockMvc.perform(post("/users/createUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O campo obrigatorio '" + requiredFields.get(i) + "' esta vazio."));

            mockMvc.perform(put("/users/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("O campo obrigatorio '" + requiredFields.get(i) + "' esta vazio."));
        }
    }
}