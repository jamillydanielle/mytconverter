package com.mytconvert.security.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mytconvert.security.entity.LoggedUser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class UserClaimsAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(

        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String claim = request.getHeader("x-user"); // recupera dados do usuario do header
        
        if (claim != null) {
            try {
                var loggedUser = new ObjectMapper().readValue(claim, LoggedUser.class); // Cria um UserDetails com o usuário encontrado

                // Cria um objeto de autenticação do Spring Security
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(loggedUser, null, loggedUser.getAuthorities());

                // Define o objeto de autenticação no contexto de segurança do Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JsonParseException e) {
                response.sendError(401);

                return;
            }
        }

        filterChain.doFilter(request, response); // Continua o processamento da requisição0
    }

}
