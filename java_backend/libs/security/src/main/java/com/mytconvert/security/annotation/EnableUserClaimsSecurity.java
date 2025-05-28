package com.mytconvert.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.mytconvert.security.configuration.UserClaimSecurityConfig;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Import(UserClaimSecurityConfig.class)
public @interface EnableUserClaimsSecurity {
    
}
