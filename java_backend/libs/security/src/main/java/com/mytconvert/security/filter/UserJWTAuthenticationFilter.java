package com.mytconvert.security.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mytconvert.security.entity.LoggedUser;
import com.mytconvert.security.service.JwtTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserJWTAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenService jwtTokenService;

    public UserJWTAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        

        String token = recoveryToken(request);
        
        // Bypass authentication for user creation and ALL conversions endpoints
        if ((request.getRequestURI().equals("/users/createUser") && request.getMethod().equals("POST")) ||
            request.getRequestURI().contains("/conversions")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (token != null) {
            try {
                LoggedUser loggedUser = jwtTokenService.getUserFromToken(token);
                
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(loggedUser, null, loggedUser.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
                response.sendError(401);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }
}