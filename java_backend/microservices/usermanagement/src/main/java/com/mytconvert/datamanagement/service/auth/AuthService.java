package com.mytconvert.datamanagement.service.auth;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.mytconvert.security.utils.JwtUtils;
import com.mytconvert.datamanagement.dto.LoginRequest;
import com.mytconvert.datamanagement.security.UserAuthenticated;
import com.mytconvert.datamanagement.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(JwtService jwtService, AuthenticationManager authenticationManager, 
                       UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(LoginRequest loginRequest) throws JsonProcessingException, JWTCreationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        
        if (passwordEncoder.matches("password", userDetails.getPassword())) {
            UserAuthenticated userAuthenticated = (UserAuthenticated) userDetails;
            String token =  jwtService.generateToken(userAuthenticated, loginRequest.isRememberMe());
            throw new PasswordNeedsChangeException("A senha precisa ser trocada", token);
        }
        
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        }

        UserAuthenticated userAuthenticated = (UserAuthenticated) userDetails;
        return jwtService.generateToken(userAuthenticated, loginRequest.isRememberMe());
    }

    public boolean isLoggedIn() { 
        try {
            return JwtUtils.isLogedUser();

        } catch (Exception e) {
            return false;
        }
    }
}