package com.sportio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for authenticated user's full profile including statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String avatarInitials;
    private String skillLevel;
    private Integer gamesPlayed;
    private String bio;
    private LocalDateTime memberSince;
    private UserStatsDto stats;
}

