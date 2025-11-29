package com.sportio.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    @Id
    private Long id;
    private String email;
    private String passwordHash;
    private String fullName;
    private String avatarUrl;
    private String avatarInitials;
    private String skillLevel;
    private Integer gamesPlayed;
    private String bio;
    private LocalDateTime memberSince;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}