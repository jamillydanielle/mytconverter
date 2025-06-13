package com.mytconvert.datamanagement.entity.convertion;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.mytconvert.datamanagement.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "convertions")
public class Convertion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private User requester;

    @Column(nullable = false)
    private String internalFileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConvertionFormat format;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Convertion(com.mytconvert.security.utils.JwtUtils.User user, String internalFileName2,
            ConvertionFormat valueOf) {
        //TODO Auto-generated constructor stub
    }
}
