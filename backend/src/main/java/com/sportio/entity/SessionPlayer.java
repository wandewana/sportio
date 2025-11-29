package com.sportio.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a player's participation in a session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("session_players")
public class SessionPlayer {
    
    @Id
    private Long id;
    
    private Long sessionId;
    
    private Long userId;
    
    private Boolean isHost;
    
    private String status;
    
    private LocalDateTime joinedAt;
    
    private String paymentStatus;
    
    private BigDecimal paymentAmount;
    
    private LocalDateTime paidAt;
}

