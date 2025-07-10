package com.mytconvert.datamanagement.controller.auth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.datamanagement.dto.ChangePasswordRequest;
import com.mytconvert.datamanagement.dto.LoginRequest;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.entity.user.UserType;
import com.mytconvert.datamanagement.service.auth.AuthService;
import com.mytconvert.datamanagement.service.auth.PasswordNeedsChangeException;
import com.mytconvert.datamanagement.service.user.UserService;
import com.mytconvert.datamanagement.utils.RequestValidator;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private static final String USER_NOT_FOUND = "Usuario nao encontrado";
    private static final String INVALID_CREDENTIALS = "Email ou senha estao incorretos. Tente novamente.";
    private static final String ACCOUNT_DISABLED = "Conta desativada";
    private static final String ACCOUNT_LOCKED = "Conta travada";
    private static final String CREDENTIALS_EXPIRED = "Credenciais expiradas";

    @Autowired
    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
    try {
        Optional<User> usuario = userService.findByEmail(loginRequest.getEmail());
        boolean isActive = false;
        boolean userExists = usuario.isPresent();
        
        if (userExists) {
            isActive = usuario.get().isActive();
        }
        
        if (!userExists) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse(USER_NOT_FOUND));
        }
        
        if (!isActive) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse(ACCOUNT_DISABLED));
        }
        
        if (userExists && isActive) {
            try {
                String token = authService.authenticate(loginRequest);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login realizado com sucesso");
                return ResponseEntity.ok(response);
            } catch (PasswordNeedsChangeException e) {
                Map<String, String> response = new HashMap<>();
                response.put("token", e.getToken());
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse(INVALID_CREDENTIALS));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(createErrorResponse("Erro de autenticação"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse("Erro interno do servidor: " + e.getMessage()));
    }
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

    @GetMapping("/is-logged-in")
    public ResponseEntity<Map<String, Boolean>> isLoggedIn() {
        Map<String, Boolean> response = new HashMap<>();
        boolean isLoggedUser = authService.isLoggedIn();
        response.put("isLoggedIn", isLoggedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password/{email}")
    public ResponseEntity<?> changePassword(@PathVariable String email, @RequestBody ChangePasswordRequest request) {
        List<String> requiredFields = Arrays.asList("newPassword");
        RequestValidator.validateFields(request, requiredFields);
        RequestValidator.validatePasswordStrength(request.getNewPassword());
    
        try {
            userService.changePassword(email, request.getNewPassword());
            return ResponseEntity.ok(createSuccessResponse("Senha atualizada com sucesso."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(USER_NOT_FOUND));
        }
    }
    
    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}