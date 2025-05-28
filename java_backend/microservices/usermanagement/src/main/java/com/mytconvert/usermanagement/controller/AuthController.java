package com.mytconvert.usermanagement.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mytconvert.usermanagement.dto.ChangePasswordRequest;
import com.mytconvert.usermanagement.dto.LoginRequest;
import com.mytconvert.usermanagement.entity.User;
import com.mytconvert.usermanagement.service.AuthService;
import com.mytconvert.usermanagement.service.PasswordNeedsChangeException;
import com.mytconvert.usermanagement.service.UserService;
import com.mytconvert.usermanagement.utils.RequestValidator;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private static final String USER_NOT_FOUND = "User not found";
    private static final String INVALID_CREDENTIALS = "The username or password is incorrect. Please try again.";
    private static final String ACCOUNT_DISABLED = "Account disabled";
    private static final String ACCOUNT_LOCKED = "Account locked";
    private static final String CREDENTIALS_EXPIRED = "Credentials expired";
    private static final String INTERNAL_ERROR = "Internal server error";

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

        if(userExists && !isActive){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse(ACCOUNT_DISABLED));
        }

        if (userExists && isActive) {
            try {
                String token = authService.authenticate(loginRequest);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            } catch (PasswordNeedsChangeException e) {
                String token = e.getToken();
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Password needs to be changed");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            } catch (LockedException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse(ACCOUNT_LOCKED));
            } catch (CredentialsExpiredException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse(CREDENTIALS_EXPIRED));
            } catch (BadCredentialsException | InsufficientAuthenticationException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse(INVALID_CREDENTIALS));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse(USER_NOT_FOUND));
        }
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(createErrorResponse("An unexpected error occurred: " + e.getMessage()));
    }
}


    @PutMapping("/change-password/{email}")
    public ResponseEntity<?> changePassword(@PathVariable String email, @RequestBody ChangePasswordRequest request) {
        List<String> requiredFields = Arrays.asList("newPassword");
        RequestValidator.validateFields(request, requiredFields);
        RequestValidator.validatePasswordStrength(request.getNewPassword());
    
        try {
            userService.changePassword(email, request.getNewPassword());
            return ResponseEntity.ok(createSuccessResponse("Password updated successfully."));
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