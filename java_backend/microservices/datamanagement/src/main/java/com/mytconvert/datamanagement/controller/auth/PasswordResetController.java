package com.mytconvert.datamanagement.controller.auth;

import com.mytconvert.datamanagement.dto.PasswordResetRequest;
import com.mytconvert.datamanagement.service.auth.PasswordResetService;
import com.mytconvert.datamanagement.utils.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/passwordReset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final RestTemplate restTemplate;
    
    // Fix the email endpoint URL
    private String sendEmailEndpoint = "http://mytconverter-emailsender-1:8085/email/email/sendPasswordResetEmail";

    @Autowired
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/resetRequest")
    public void requestPasswordReset(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        
        try {
            // Criar token e obter URL de redefinição
            String resetLink = passwordResetService.createPasswordResetTokenForUser(email);
            
            System.out.println("Tentou mandar email? " + email);
            System.out.println("Reset link: " + resetLink);

            sendPasswordResetRequestEmail(email, resetLink);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResponseEntity<String> sendPasswordResetRequestEmail(String email, String resetLink) {
        ResponseEntity<String> emailResponse = null;
        int maxRetries = 3;
        int retryCount = 0;
        int retryDelayMs = 1000; // Start with 1 second delay
        
        while (retryCount < maxRetries) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(sendEmailEndpoint)
                        .queryParam("email", email)
                        .queryParam("resetLink", resetLink);
                
                System.out.println("Attempting to send email to: " + email + ", attempt " + (retryCount + 1) + " of " + maxRetries);
                System.out.println("Using endpoint: " + builder.toUriString());
                
                // Envia a solicitação POST para o serviço de email
                emailResponse = restTemplate.postForEntity(
                        builder.toUriString(),
                        null, // não precisa de corpo na requisição pois os parâmetros vão na URL
                        String.class);
                
                // If successful, break out of retry loop
                System.out.println("Email sent successfully to: " + email);
                return emailResponse;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    System.out.println("Failed to send email after " + maxRetries + " attempts");
                    e.printStackTrace();
                } else {
                    // Log retry attempt
                    System.out.println("Email sending failed, retrying in " + retryDelayMs + "ms. Attempt " + retryCount + " of " + maxRetries);
                    try {
                        Thread.sleep(retryDelayMs);
                        // Exponential backoff
                        retryDelayMs *= 2;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return emailResponse;
    }
    
    @PostMapping("/validateToken")
    public ResponseEntity<Map<String, String>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        
        if (token == null || token.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token é obrigatório");
        }
        
        System.out.println("Validating token in controller: " + token);
        
        try {
            passwordResetService.validatePasswordResetToken(token);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Token válido");
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            System.out.println("Token validation failed: " + e.getReason());
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(response);
        }
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody PasswordResetRequest request) {
        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token é obrigatório");
        }
        
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nova senha é obrigatória");
        }
        
        // Validar força da senha
        RequestValidator.validatePasswordStrength(request.getNewPassword());
        
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Senha redefinida com sucesso");
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(response);
        }
    }
}