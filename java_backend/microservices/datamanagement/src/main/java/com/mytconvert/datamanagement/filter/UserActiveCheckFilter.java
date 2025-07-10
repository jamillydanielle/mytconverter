package com.mytconvert.datamanagement.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mytconvert.datamanagement.entity.user.User;
import com.mytconvert.datamanagement.repository.user.UserRepository;
import com.mytconvert.security.entity.LoggedUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que verifica se o usuário autenticado está ativo.
 * Se o usuário estiver desativado, a requisição é rejeitada com status 401.
 */
@Component
public class UserActiveCheckFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Autowired
    public UserActiveCheckFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Bypass para endpoints públicos
        if ((request.getRequestURI().equals("/users/createUser") && request.getMethod().equals("POST")) ||
            request.getRequestURI().contains("/conversions") ||
            request.getRequestURI().equals("/users/activate")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Verificar se há um usuário autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof LoggedUser) {
            LoggedUser loggedUser = (LoggedUser) authentication.getPrincipal();
            
            // Verificar se o usuário está ativo
            Optional<User> userOpt = userRepository.findById(loggedUser.getId());
            if (userOpt.isPresent() && !userOpt.get().isActive()) {
                // Usuário está desativado, rejeitar a requisição
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User account is deactivated");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}