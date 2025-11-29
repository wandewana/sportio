package com.sportio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for viewing other users' public profiles.
 * Excludes sensitive information like email.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfileDto {
    
    private Long id;
    private String fullName;
    private String avatarUrl;
    private String avatarInitials;
    private String skillLevel;
    private Integer gamesPlayed;
    private String bio;
    private LocalDateTime memberSince;
}

