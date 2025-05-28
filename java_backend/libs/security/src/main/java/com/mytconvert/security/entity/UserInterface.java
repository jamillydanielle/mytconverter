package com.mytconvert.security.entity;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface UserInterface 
{
    public Long getId();
    
    public String getName();
    
    public String getEmail();
    
    public String getType();
    
    default public String getPassword() {
        return null;
    }


    public Collection<? extends GrantedAuthority> getAuthorities();
}
