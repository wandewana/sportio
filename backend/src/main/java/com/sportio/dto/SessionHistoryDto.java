package com.sportio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for session history entries showing user's past and current sessions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionHistoryDto {
    
    private Long sessionId;
    private String sportType;
    private String title;
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private String status;
    private Boolean isHost;
    private LocalDateTime joinedAt;
}

