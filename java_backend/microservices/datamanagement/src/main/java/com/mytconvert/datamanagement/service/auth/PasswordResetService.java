package com.mytconvert.datamanagement.service.auth;

import com.mytconvert.datamanagement.entity.user.PasswordResetToken;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.repository.user.PasswordResetTokenRepository;
import com.mytconvert.datamanagement.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.password-reset.token.expiration:30}")
    private int tokenExpirationMinutes;

    @Autowired
    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um token de recuperação de senha para o usuário com o email fornecido
     * @param email Email do usuário
     * @return O token gerado e a URL para redefinição de senha
     */
    public String createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        
        // Invalidar tokens anteriores do usuário
        List<PasswordResetToken> existingTokens = tokenRepository.findByUser(user);
        System.out.println("Found " + existingTokens.size() + " existing tokens for user: " + email);
        existingTokens.forEach(token -> {
            token.setUsed(true);
            tokenRepository.save(token);
        });
        
        // Gerar novo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(tokenExpirationMinutes)
        );
        
        PasswordResetToken savedToken = tokenRepository.save(passwordResetToken);
        System.out.println("Created password reset token: " + token + " for user: " + email);
        System.out.println("Token saved with ID: " + savedToken.getId());
        System.out.println("Token expiration: " + passwordResetToken.getExpiryDate());
        
        // Retornar a URL para redefinição de senha
        return frontendUrl + "/reset-password?token=" + token;
    }
    
    /**
     * Valida um token de recuperação de senha
     * @param token O token a ser validado
     * @return O usuário associado ao token, se válido
     */
    public User validatePasswordResetToken(String token) {
        System.out.println("Validating token in service: " + token);
        
        // Check if token is null or empty
        if (token == null || token.trim().isEmpty()) {
            System.out.println("Token is null or empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido");
        }
        
        // Try to find the token in the database
        System.out.println("Searching for token in database");
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        
        if (resetTokenOpt.isEmpty()) {
            System.out.println("Token not found in database: " + token);
            
            // Debug: List all tokens in the database
            List<PasswordResetToken> allTokens = tokenRepository.findAll();
            System.out.println("Total tokens in database: " + allTokens.size());
            if (allTokens.size() < 10) {
                allTokens.forEach(t -> System.out.println("DB Token: " + t.getToken()));
            }
            
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido");
        }
        
        PasswordResetToken resetToken = resetTokenOpt.get();
        System.out.println("Token found in database. Token ID: " + resetToken.getId());
        System.out.println("Associated user: " + resetToken.getUser().getEmail());
        
        if (resetToken.isExpired()) {
            System.out.println("Token expired. Expiry date: " + resetToken.getExpiryDate() + ", Current time: " + LocalDateTime.now());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado");
        }
        
        if (resetToken.isUsed()) {
            System.out.println("Token already used");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token já utilizado");
        }
        
        System.out.println("Token valid for user: " + resetToken.getUser().getEmail());
        return resetToken.getUser();
    }
    
    /**
     * Redefine a senha do usuário usando um token de recuperação
     * @param token O token de recuperação
     * @param newPassword A nova senha
     */
    public void resetPassword(String token, String newPassword) {
        System.out.println("Attempting to reset password with token: " + token);
        
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isEmpty()) {
            System.out.println("Token not found in database: " + token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido");
        }
        
        PasswordResetToken resetToken = resetTokenOpt.get();
        
        if (resetToken.isExpired()) {
            System.out.println("Token expired. Expiry date: " + resetToken.getExpiryDate() + ", Current time: " + LocalDateTime.now());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expirado");
        }
        
        if (resetToken.isUsed()) {
            System.out.println("Token already used");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token já utilizado");
        }
        
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Marcar o token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        System.out.println("Password reset successful for user: " + user.getEmail());
    }
}