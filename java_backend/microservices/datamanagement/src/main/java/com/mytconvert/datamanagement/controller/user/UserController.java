package com.mytconvert.datamanagement.controller.user;

import com.mytconvert.datamanagement.dto.UserData;
import com.mytconvert.datamanagement.dto.UserSessionDTO;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserType;
import com.mytconvert.datamanagement.exception.ErrorResponse;
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
import com.mytconvert.security.utils.JwtUtils;

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
        User user = userService.getUserById(id);

        if (user != null) {
            User userInstance = user;

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

    @PutMapping("/edit")
    public ResponseEntity<String> updateUser(@RequestBody Map<String, Object> payload) {

        List<String> requiredFields = Arrays.asList("name", "email");
        RequestValidator.validateFieldsForMap(payload, requiredFields);


        User updatedUser = userService.updateUser(payload);

        return ResponseEntity.status(HttpStatus.OK)
                .body("{\"message\": \"Usuario atualizado\", \"userName\": \"" + updatedUser.getName() + "\"}");
    }

    @PutMapping("/deactivate")
    public ResponseEntity<User> deactivateUser() {
        User deactivatedUser = userService.deactivateUser(JwtUtils.getCurrentUserId().get());
        return ResponseEntity.ok(deactivatedUser);
    }

    @PutMapping("/activate")
    public ResponseEntity<User> activateUser() {
        User activateUser = userService.activateUser(JwtUtils.getCurrentUserId().get());
        return ResponseEntity.ok(activateUser);
    }

    @GetMapping("/getCurrentUserData")
    public ResponseEntity<?> getCurrentUserData(){
        User user = userService.getUserById(JwtUtils.getCurrentUserId().get());
        if (user != null) {
            User userInstance = user;

            UserData userData = new UserData(
                    userInstance.getName(),
                    userInstance.getEmail());
            return ResponseEntity.ok(userData);
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("User not found", "The requested user could not be found"));
    }
}