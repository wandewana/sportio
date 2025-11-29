package com.sportio.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing a sports session.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sessions")
public class Session {
    
    @Id
    private Long id;
    
    private Long hostId;
    
    private String sportType;
    
    private String title;
    
    private String description;
    
    private LocalDate date;
    
    private LocalTime timeStart;
    
    private LocalTime timeEnd;
    
    private Integer playersNeeded;
    
    private String visibility;
    
    private String status;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private LocalDateTime createdAt;
}

