package com.mytconvert.datamanagement.controller.user;

import com.mytconvert.datamanagement.dto.UserData;
import com.mytconvert.datamanagement.dto.UserSessionDTO;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserType;
import com.mytconvert.datamanagement.service.user.UserService;
import com.mytconvert.datamanagement.service.user.UserSessionService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mytconvert.datamanagement.utils.RequestValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserSessionService userSessionService;

    @Autowired
    public UserController(UserService userService, UserSessionService userSessionService) {
        this.userService = userService;
        this.userSessionService = userSessionService;
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody Map<String, Object> payload) {
        
        List<String> requiredFields = Arrays.asList("name", "email", "password");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String userName = (String) payload.get("name");
        String userEmail = (String) payload.get("email");
        String senha = (String) payload.get("password");

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

    @GetMapping("/sessions")
    public ResponseEntity<List<UserSessionDTO>> getUserSessions() {
        List<User> users = userService.getAllUsers();
        
        List<UserSessionDTO> sessionDTOs = users.stream()
            .map(user -> {
                LocalDateTime lastSession = userSessionService.getLastSessionTime(user);
                
                return new UserSessionDTO(
                    user.getId(),
                    user.getName(),
                    lastSession
                );
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(sessionDTOs);
    }

    private UserData mapUserToUserData(User user) {
        return new UserData(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getType(),
                user.getDeactivatedAt());  // Adicionado o campo deactivatedAt
    }

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
                    type,
                    userInstance.getDeactivatedAt());  // Adicionado o campo deactivatedAt
            return ResponseEntity.ok(userData);
        }
        return ResponseEntity.status(404).build();
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<UserSessionDTO> getUserSessionById(@PathVariable Long id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            LocalDateTime lastSession = userSessionService.getLastSessionTime(user);
            
            UserSessionDTO sessionDTO = new UserSessionDTO(
                user.getId(),
                user.getName(),
                lastSession
            );
            
            return ResponseEntity.ok(sessionDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> payload) {

        List<String> requiredFields = Arrays.asList("name", "email", "password");
        RequestValidator.validateFieldsForMap(payload, requiredFields);

        String userName = (String) payload.get("name");
        String userEmail = (String) payload.get("email");
        String senhaPadrao = "password";

        User user = new User(userName, userEmail, senhaPadrao, UserType.USER);

        User updatedUser = userService.updateUser(id, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body("{\"message\": \"Usuario atualizado\", \"userName\": \"" + updatedUser.getName() + "\"}");
    }

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