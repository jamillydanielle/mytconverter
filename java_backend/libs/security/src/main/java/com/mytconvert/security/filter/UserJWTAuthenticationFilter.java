package com.mytconvert.security.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
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
        String token = recoveryToken(request); // Recupera o token do cabeçalho Authorization da requisição
        //System.out.println("criatura!!!");
        //System.out.println(token);
        
        if (token != null) {
            try {
                var loggedUser = jwtTokenService.getUserFromToken(token); // Cria um UserDetails com o usuário encontrado
                
                // Cria um objeto de autenticação do Spring Security
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(loggedUser, null, loggedUser.getAuthorities());

                // Define o objeto de autenticação no contexto de segurança do Spring Security

                

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
                response.sendError(401);
                return;
            }
        }

        filterChain.doFilter(request, response); // Continua o processamento da requisição0
    }

    // Recupera o token do cabeçalho Authorization da requisição
    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }

        return null;
    }

}