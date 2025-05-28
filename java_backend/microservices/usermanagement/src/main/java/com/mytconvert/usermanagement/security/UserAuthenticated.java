package com.mytconvert.usermanagement.security;


import com.mytconvert.usermanagement.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;


import java.util.Collection;
import java.util.List;


public class UserAuthenticated implements UserDetails {
    private final User user;

    public UserAuthenticated(User user) {
        this.user = user; // Recebe o usuário do banco de dados identificado pelo email
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getType().toString())); // Retorna as claims do usuário (USER, ADMIN)
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // No contexto da Basic Authentication, o username do usuário autenticado deve ser um valor único, neste caso, o email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true; // Utilizar esse método posteriormente para NÃO autorizar a autenticação de usuários desativados
    }
    public User getUser() {
        return user;
    }
}