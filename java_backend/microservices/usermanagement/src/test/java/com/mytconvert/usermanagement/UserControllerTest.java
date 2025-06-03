// package com.mytconvert.usermanagement;

// import com.mytconvert.usermanagement.entity.User;
// import com.mytconvert.usermanagement.entity.UserType;
// import com.mytconvert.usermanagement.repository.UserRepository;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;

// import jakarta.transaction.Transactional;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import java.util.Arrays;
// import java.util.List;
// import static org.hamcrest.Matchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// @Transactional // Para garantir que cada teste é isolado
// class UserControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private UserRepository userRepository;

//     @BeforeEach
//     void setUp() {
//         Limpar e adicionar um usuário ao banco de dados em memória antes de cada teste
//         userRepository.deleteAll();
//         User user = new User("John Doe", "john@example.br", "password", UserType.USER);
//         userRepository.save(user); // Persistir o usuário no banco de dados em memória
//     }

//     private User createTestUser(String name, String email, String type) {
       
//         UserType userType = UserType.valueOf(type.toUpperCase());
    
//         User user = new User();
//         user.setName(name);
//         user.setEmail(email);
//         user.setType(userType);
//         user.setPassword("encodedPassword");
    
//         return userRepository.save(user);
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testCheckUser() throws Exception {
//         String username = "john@example.br";

//         mockMvc.perform(get("/users/checkUser")
//                 .param("username", username))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("true"));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testListUsers() throws Exception {
//         mockMvc.perform(get("/users/list")
//                 .param("page", "0")
//                 .param("size", "1"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.content", hasSize(1)))
//                 .andExpect(jsonPath("$.content[0].name", is("John Doe")));
//     }

//     @Test
//     @WithMockUser(username = "admin", roles = {"ADMIN"})
//     void testGetUserById() throws Exception {
//         Long userId = userRepository.findAll().get(0).getId(); // Obter o ID do usuário persistido

//         mockMvc.perform(get("/users/list/" + userId))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name", is("John Doe")));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testCreateUser() throws Exception {
//         String payload = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.br\",\"type\": \"USER\" }";

//         mockMvc.perform(post("/users/createUser")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isCreated())
//                 .andExpect(jsonPath("$.message", containsString("User created")))
//                 .andExpect(jsonPath("$.userName", is("Jane Doe")));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void createUser_ExistingEmail() throws Exception {
//         createTestUser("Jane Doe", "jane@example.br", "USER");

//         String payload = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.br\", \"type\": \"USER\"}";

//         mockMvc.perform(post("/users/createUser")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.error", containsString("This email already belongs to another user.")));
//     }

   

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testUpdateUser() throws Exception {
//         User user = createTestUser("John Doe", "john1@example.br", "USER");

//         String payload = "{ \"name\": \"John Updated\", \"email\": \"joh1n@example.br\", \"type\": \"USER\"}";

//         mockMvc.perform(put("/users/edit/" + user.getId())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message", containsString("User updated")))
//                 .andExpect(jsonPath("$.userName", is("John Updated")));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = { "ADMIN" })
//     void editUser_ExistingEmail() throws Exception {
//         createTestUser("Jane Doe", "jane@example.br", "USER");
//         User editUser = createTestUser("Jane Doe", "jane1@example.br", "USER");

//         String payload = "{ \"name\": \"Jane Doe\", \"email\": \"jane@example.br\", \"type\": \"USER\"}";

//         mockMvc.perform(put("/users/edit/" + editUser.getId())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.error", containsString("This email already belongs to another user.")));
//     }
  
//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testDeleteUser() throws Exception {
//         Long userId = userRepository.findAll().get(0).getId();

//         mockMvc.perform(delete("/users/" + userId))
//                 .andExpect(status().isNoContent());
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testDeactivateUser() throws Exception {
//         Long userId = userRepository.findAll().get(0).getId();

//         mockMvc.perform(put("/users/" + userId + "/deactivate"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name", is("John Doe")));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testActivateUser() throws Exception {
//         Obter o ID do usuário persistido
//         Long userId = userRepository.findAll().get(0).getId();

//         Desativar o usuário primeiro, para que possamos testá-lo sendo ativado
//         mockMvc.perform(put("/users/" + userId + "/deactivate"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name", is("John Doe")));

//         Agora tente ativar o usuário
//         mockMvc.perform(put("/users/" + userId + "/activate"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name", is("John Doe")));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testCreateLocalAdminUser() throws Exception {
//         String payload = "{ \"name\": \"Admin User\", \"email\": \"admi@example.br\", \"type\": \"ADMIN\" }";

//         mockMvc.perform(post("/users/local-admin")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isCreated())
//                 .andExpect(content().string(containsString("Local admin user created successfully: Admin User")));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void testCreateLocalAdminUserWithExistingEmail() throws Exception {
//         Primeiro, crie um usuário admin
//         createTestUser("Admin", "admi@example.br",  "ADMIN");

//         String payload = "{ \"name\": \"Admin User\", \"email\": \"admi@example.br\", \"type\": \"ADMIN\" }";

//         Tente criar outro usuário com o mesmo email
//         mockMvc.perform(post("/users/local-admin")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(content().string(containsString("An admin user with this email already exists.")));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void checkAllPossibleEmptyFieldsWhenCreatingOrUpdatingUsers() throws Exception {
//         String name = "Jane Doe";
//         String email = "jane@example.br";
//         String type = "USER";
    

//         List<String> requiredFields = Arrays.asList("name", "email", "type");
    
//         for (int i = 0; i < requiredFields.size(); i++) {
//             String currentName = (i == 0) ? "" : name;
//             String currentEmail = (i == 1) ? "" : email;
//             String currentType = (i == 2) ? "" : type;
    
//             String payload = String.format(
//                 "{ \"name\": \"%s\", \"email\": \"%s\", \"type\": \"%s\"}",
//                 currentName,
//                 currentEmail,
//                 currentType
//             );
    
//             mockMvc.perform(post("/users/createUser")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.error").value("The required field '" + requiredFields.get(i) + "' is empty."));

//             mockMvc.perform(put("/users/edit/1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.error").value("The required field '" + requiredFields.get(i) + "' is empty."));
//         }
//     }

//     @Test
//     @WithMockUser(username = "user", roles = {"ADMIN"})
//     void checkAllPossibleEmptyFieldsWhenCreatingAdmin() throws Exception {
//         String name = "Jane Doe";
//         String email = "jane@example.br";

//         List<String> requiredFields = Arrays.asList("name", "email");
    
//         for (int i = 0; i < requiredFields.size(); i++) {
//             String currentName = (i == 0) ? "" : name;
//             String currentEmail = (i == 1) ? "" : email;
            
    
//             String payload = String.format(
//                 "{ \"name\": \"%s\", \"email\": \"%s\"}",
//                 currentName,
//                 currentEmail
//             );
    
//             mockMvc.perform(post("/users/local-admin")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(payload))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.error").value("The required field '" + requiredFields.get(i) + "' is empty."));
//         }
//     }
// }