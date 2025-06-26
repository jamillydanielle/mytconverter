package com.mytconvert.datamanagement.security;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mytconvert.datamanagement.entity.user.User;

import org.springframework.security.core.GrantedAuthority;


import java.util.Collection;
import java.util.List;


public class UserAuthenticated implements UserDetails {
    private final User user;

    public UserAuthenticated(User user) {
        this.user = user; 
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getType().toString()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return true; 
    }
    public User getUser() {
        return user;
    }
}