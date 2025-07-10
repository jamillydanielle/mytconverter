package com.mytconvert.datamanagement.service.auth;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.mytconvert.security.utils.JwtUtils;
import com.mytconvert.datamanagement.dto.LoginRequest;
import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.repository.user.UserRepository;
import com.mytconvert.datamanagement.security.UserAuthenticated;
import com.mytconvert.datamanagement.security.UserDetailsServiceImpl;
import com.mytconvert.datamanagement.service.user.UserSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionService userSessionService;
    private final UserRepository userRepository;

    @Autowired
    public AuthService(JwtService jwtService, AuthenticationManager authenticationManager, 
                       UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder,
                       UserSessionService userSessionService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userSessionService = userSessionService;
        this.userRepository = userRepository;
    }

    public String authenticate(LoginRequest loginRequest) throws JsonProcessingException, JWTCreationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        
         try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciais invalidas");
        }

        UserAuthenticated userAuthenticated = (UserAuthenticated) userDetails;
        String token = jwtService.generateToken(userAuthenticated, loginRequest.isRememberMe());
        
        // Registrar a sessão do usuário
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        userOpt.ifPresent(user -> userSessionService.createSession(user));
        
        return token;
    }

    public boolean isLoggedIn() { 
        try {
            return JwtUtils.isLogedUser();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método para realizar logout do usuário
     * Como estamos usando JWT, não há necessidade de invalidar o token no servidor,
     * mas registramos o logout para fins de rastreamento de sessão
     * 
     * @param token O token JWT do usuário
     */
    public void logout(String token) {
        // No caso de JWT, não precisamos invalidar o token no servidor
        // O cliente deve simplesmente descartar o token
        
        // Podemos registrar o logout para fins de auditoria ou estatísticas
        // mas não implementaremos isso agora para simplificar
        
        // Se no futuro quisermos implementar uma lista negra de tokens,
        // podemos fazer isso aqui
    }
}