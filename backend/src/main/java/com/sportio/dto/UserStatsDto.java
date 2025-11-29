package com.sportio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO containing aggregated user statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {
    
    /**
     * Total number of session bookings/participations.
     */
    private Integer totalBookings;
    
    /**
     * Average rating received from other players.
     * Placeholder: returns 0.0 until ratings feature is implemented.
     */
    @Builder.Default
    private Double avgRating = 0.0;
    
    /**
     * Number of friends/connections.
     * Placeholder: returns 0 until friends feature is implemented.
     */
    @Builder.Default
    private Integer friendsCount = 0;
}

