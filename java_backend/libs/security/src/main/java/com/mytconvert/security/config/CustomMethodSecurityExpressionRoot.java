package com.mytconvert.security.config;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * Check if the current user has the specified type as an authority
     * 
     * @param type The user type to check for
     * @return true if the user has the specified type, false otherwise
     */
    public boolean hasType(String type) {
        if (type == null || type.isEmpty()) {
            return false;
        }
        
        // Check if the user has the exact type as an authority
        for (GrantedAuthority authority : getAuthentication().getAuthorities()) {
            if (authority.getAuthority().equals(type)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }
    
    public void setThis(Object target) {
        this.target = target;
    }
}