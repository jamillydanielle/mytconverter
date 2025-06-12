package com.mytconvert.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class JwtUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * Get the current authenticated user data
     *
     * @return Optional containing authenticated user data, or empty if not authenticated
     */
    public static Optional<UserData> getCurrentUserData() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            
            return extractUserDataFromRequest(request);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get the current authenticated user ID
     *
     * @return Optional containing authenticated user ID, or empty if not authenticated
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUserData()
                .map(UserData::getUser)
                .map(User::getId);
    }

    /**
     * Get the current authenticated user
     *
     * @return Optional containing authenticated user, or empty if not authenticated
     */
    public static Optional<User> getCurrentUser() {
        return getCurrentUserData()
                .map(UserData::getUser);
    }
    
  
    /**
     * Get the current authenticated user's type
     *
     * @return Optional containing user type, or empty if not authenticated
     */
    public static Optional<String> getCurrentUserType() {
        return getCurrentUser()
                .map(User::getType);
    }
    
    
    /**
     * Check if the current user is a USER
     *
     * @return true if user has type USER, false otherwise
     */
    public static boolean isUser() {
        return getCurrentUserType()
                .map(type -> "USER".equalsIgnoreCase(type))
                .orElse(false);
    }
    
    
    
    /**
     * Process the request to extract and set user authentication
     *
     * @param request The HTTP request
     * @return Optional containing authenticated user data, or empty if not authenticated
     */
    public static Optional<UserData> extractUserDataFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        
        String token = authHeader.substring(BEARER_PREFIX.length());
        
        if (token.isEmpty()) {
            return Optional.empty();
        }
        
        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String userDataJson = decodedJWT.getClaim("user").asString();
            
            if (userDataJson == null || userDataJson.isEmpty()) {
                return Optional.empty();
            }
            
            UserData userData = objectMapper.readValue(userDataJson, UserData.class);
            
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Authentication authentication = createAuthentication(userData);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
            return Optional.of(userData);
        } catch (Exception e) {
            System.err.println("Error decoding JWT token: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Create Spring Security Authentication object from user data
     *
     * @param userData User data from JWT
     * @return Authentication object for Spring Security
     */
    private static Authentication createAuthentication(UserData userData) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
             
        if (userData.getUser() != null && userData.getUser().getType() != null) {
            String type = userData.getUser().getType();
            authorities.add(new SimpleGrantedAuthority("TYPE_" + type.toUpperCase()));
        }
        
              
        if (userData.getAuthorities() != null) {
            for (Authority authority : userData.getAuthorities()) {
                authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
            }
        }
        
        return new UsernamePasswordAuthenticationToken(
                userData.getUser(), 
                null, 
                authorities 
        );
    }

    public static boolean isLogedUser() {
        if(!getCurrentUserData().isPresent()) return false;
        
        return getCurrentUserData()
            .get().isAccountNonExpired();
    }

   
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserData {
        private User user;
        private String username;
        private boolean enabled;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private Authority[] authorities;
    }

 
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private Long id;
        private String name;
        private String email;
        private String type;
        private boolean active;
    }

  
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Authority {
        private String authority;
    }
}