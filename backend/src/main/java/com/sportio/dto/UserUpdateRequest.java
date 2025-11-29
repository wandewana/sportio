package com.sportio.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user profile information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    private String fullName;
    
    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    private String avatarUrl;
    
    @Pattern(regexp = "^(Beginner|Intermediate|Advanced|All levels)$", 
             message = "Skill level must be one of: Beginner, Intermediate, Advanced, All levels")
    private String skillLevel;
    
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
}

