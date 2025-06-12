package com.mytconvert.usermanagement.controller;

import com.mytconvert.usermanagement.dto.UserData;
import com.mytconvert.usermanagement.entity.User;
import com.mytconvert.usermanagement.entity.UserType;
import com.mytconvert.usermanagement.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mytconvert.usermanagement.utils.RequestValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody Map<String, String> payload) {
        
        List<String> requiredFields = Arrays.asList("name", "email", "password");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String userName = payload.get("name");
        String userEmail = payload.get("email");
        String senha = payload.get("password");

        User user = new User(userName, userEmail, senha, UserType.USER);

        User createdUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("{\"message\": \"Usuario cadastrado\", \"userName\": \"" + createdUser.getName() + "\"}");
    }

    @GetMapping("/checkUser")
    public ResponseEntity<?> checkUser(@RequestParam String username) {
        boolean userExists = userService.findByEmail(username).isPresent();
        return ResponseEntity.ok().body(userExists);
    }

    /**
     * GET /users
     * Retorna uma lista paginada de usuários
     *
     * @param pageable a informação de paginação (query parameters page e size da
     *                 requisição)
     * @return um ResponseEntity contendo uma página de usuários
     */
    /*
     * @GetMapping("/list")
     * public ResponseEntity<Page<User>> listUsers(@PageableDefault(size = 10)
     * Pageable pageable) {
     * Page<UserData> users = userService.listUsers(pageable);
     * return ResponseEntity.ok(users);
     * }
     */

    @GetMapping("/list")
    public ResponseEntity<Page<UserData>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> userPage = userService.listUsers(pageable);

        List<UserData> userDataList = userPage.getContent().stream()
                .map(this::mapUserToUserData)
                .collect(Collectors.toList());

        Page<UserData> userDataPage = new PageImpl<>(userDataList, pageable, userPage.getTotalElements());

        return ResponseEntity.ok(userDataPage);
    }

    private UserData mapUserToUserData(User user) {
        return new UserData(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getType());
    }

    /**
     * GET /users/{id}
     * Retorna um usuário pelo ID
     *
     * @param id o ID do usuário
     * @return um ResponseEntity contendo o usuário ou 404 Not Found
     */
    @GetMapping("/list/{id}")
    public ResponseEntity<UserData> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            User userInstance = user.get();

            UserType type = userInstance.getType();

            UserData userData = new UserData(
                    userInstance.getId(),
                    userInstance.getName(),
                    userInstance.getEmail(),
                    type);
            return ResponseEntity.ok(userData);
        }
        return ResponseEntity.status(404).build();
    }

    /**
     * PUT /users/{id}
     * Atualiza um usuário existente
     *
     * @param id   o ID do usuário a ser atualizado
     * @param user o objeto do usuário com os novos dados
     * @return um ResponseEntity contendo o usuário atualizado ou 404 Not Found
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody Map<String, String> payload) {

        List<String> requiredFields = Arrays.asList("name", "email", "password");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String userName = payload.get("name");
        String userEmail = payload.get("email");
        String senhaPadrao = "password";

        User user = new User(userName, userEmail, senhaPadrao, UserType.USER);

        User updatedUser = userService.updateUser(id, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body("{\"message\": \"User updated\", \"userName\": \"" + updatedUser.getName() + "\"}");
    }

    /**
     * DELETE /users/{id}
     * Exclui um usuário pelo ID
     *
     * @param id o ID do usuário a ser excluído
     * @return um ResponseEntity com status 204 No Content ou 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        User deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(deactivatedUser);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        User activateUser = userService.activateUser(id);
        return ResponseEntity.ok(activateUser);
    }
}
