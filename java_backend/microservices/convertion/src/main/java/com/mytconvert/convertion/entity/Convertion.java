package com.mytconvert.convertion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "convertions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "convertion_type", discriminatorType = DiscriminatorType.STRING)
public class Convertion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "url", nullable = false)
    private String youtubeUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    public Convertion(Long userId, String youtubeUrl){
        this.userId = userId;
        this.youtubeUrl = youtubeUrl;
    }

    public boolean isCreated() {
        return createdAt != null;
    }

    public boolean isEdited() {
        return editedAt != null;
    }

}