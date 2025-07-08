package com.mytconvert.datamanagement.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime loginTime;
    
    @Column
    private LocalDateTime logoutTime;
    
    @Column(nullable = false)
    private boolean active;
    
    @PrePersist
    protected void onCreate() {
        loginTime = LocalDateTime.now();
        active = true;
    }
    
    public UserSession(User user) {
        this.user = user;
        this.loginTime = LocalDateTime.now();
        this.active = true;
    }
}