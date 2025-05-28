package com.mytconvert.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class UserDetailImpl implements UserDetails
{
    private UserInterface userInterface;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userInterface.getAuthorities();
    }

    @Override
    public String getPassword() {
        return userInterface.getPassword();
    }

    @Override
    public String getUsername() {
        return userInterface.getName();
    }

}
