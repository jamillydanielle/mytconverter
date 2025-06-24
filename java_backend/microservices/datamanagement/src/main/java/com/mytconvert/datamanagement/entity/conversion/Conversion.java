package com.mytconvert.datamanagement.entity.conversion;

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
@Table(name = "conversions")
public class Conversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(nullable = false)
    private String internalFileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversionFormat format;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long length; // New attribute for media length

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructor for direct entity creation
    public Conversion(User requester, String internalFileName, ConversionFormat format, Long length) {
        this.requester = requester;
        this.internalFileName = internalFileName;
        this.format = format;
        this.length = length;
    }

    // Constructor for JWT user conversion - this needs to be properly implemented
    public Conversion(com.mytconvert.security.utils.JwtUtils.User jwtUser, String internalFileName, ConversionFormat format, Long length) {
        // Note: In a real implementation, you would need to convert the JWT user to an entity User
        // This might require a user repository lookup
        this.internalFileName = internalFileName;
        this.format = format;
        this.length = length;
        // The requester should be set after looking up the User entity
    }
}