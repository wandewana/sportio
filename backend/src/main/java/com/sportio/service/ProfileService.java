package com.sportio.service;

import com.sportio.dto.*;
import com.sportio.entity.Session;
import com.sportio.entity.SessionPlayer;
import com.sportio.entity.User;
import com.sportio.exception.UserNotFoundException;
import com.sportio.repository.SessionPlayerRepository;
import com.sportio.repository.SessionRepository;
import com.sportio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Service for user profile operations including stats calculation and session history.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    
    private final UserRepository userRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final SessionRepository sessionRepository;
    
    /**
     * Get full profile for authenticated user including statistics.
     *
     * @param userId the user's ID
     * @return Mono containing the full user profile with stats
     */
    public Mono<UserProfileDto> getFullProfile(Long userId) {
        log.info("Fetching full profile for user: {}", userId);
        
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .flatMap(user -> calculateUserStats(userId)
                        .map(stats -> mapToUserProfileDto(user, stats)));
    }
    
    /**
     * Get public profile for viewing other users.
     *
     * @param userId the user's ID
     * @return Mono containing the public profile (excludes email)
     */
    public Mono<UserPublicProfileDto> getPublicProfile(Long userId) {
        log.info("Fetching public profile for user: {}", userId);
        
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .map(this::mapToUserPublicProfileDto);
    }
    
    /**
     * Calculate aggregated statistics for a user.
     *
     * @param userId the user's ID
     * @return Mono containing the user statistics
     */
    public Mono<UserStatsDto> calculateUserStats(Long userId) {
        log.debug("Calculating stats for user: {}", userId);
        
        return sessionPlayerRepository.countByUserId(userId)
                .map(totalBookings -> UserStatsDto.builder()
                        .totalBookings(totalBookings.intValue())
                        .avgRating(0.0)  // Placeholder until ratings feature
                        .friendsCount(0)  // Placeholder until friends feature
                        .build())
                .defaultIfEmpty(UserStatsDto.builder()
                        .totalBookings(0)
                        .avgRating(0.0)
                        .friendsCount(0)
                        .build());
    }
    
    /**
     * Get user's session participation history.
     *
     * @param userId the user's ID
     * @return Flux of session history entries ordered by date descending
     */
    public Flux<SessionHistoryDto> getSessionHistory(Long userId) {
        log.debug("Fetching session history for user: {}", userId);
        
        return sessionPlayerRepository.findByUserId(userId)
                .flatMap(sessionPlayer -> sessionRepository.findById(sessionPlayer.getSessionId())
                        .map(session -> mapToSessionHistoryDto(session, sessionPlayer)))
                .sort((a, b) -> {
                    // Sort by date descending (most recent first)
                    if (a.getDate() == null && b.getDate() == null) return 0;
                    if (a.getDate() == null) return 1;
                    if (b.getDate() == null) return -1;
                    return b.getDate().compareTo(a.getDate());
                });
    }
    
    /**
     * Update user profile with provided fields.
     *
     * @param userId the user's ID
     * @param request the update request
     * @return Mono containing the updated user profile
     */
    public Mono<UserProfileDto> updateProfile(Long userId, UserUpdateRequest request) {
        log.info("Updating profile for user: {}", userId);
        
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .flatMap(user -> {
                    updateUserFields(user, request);
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .flatMap(user -> calculateUserStats(userId)
                        .map(stats -> mapToUserProfileDto(user, stats)));
    }
    
    private void updateUserFields(User user, UserUpdateRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName().trim());
            user.setAvatarInitials(generateInitials(request.getFullName()));
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getSkillLevel() != null) {
            user.setSkillLevel(request.getSkillLevel());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
    }
    
    private String generateInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
    
    private UserProfileDto mapToUserProfileDto(User user, UserStatsDto stats) {
        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .avatarInitials(user.getAvatarInitials())
                .skillLevel(user.getSkillLevel())
                .gamesPlayed(user.getGamesPlayed())
                .bio(user.getBio())
                .memberSince(user.getMemberSince())
                .stats(stats)
                .build();
    }
    
    private UserPublicProfileDto mapToUserPublicProfileDto(User user) {
        return UserPublicProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .avatarInitials(user.getAvatarInitials())
                .skillLevel(user.getSkillLevel())
                .gamesPlayed(user.getGamesPlayed())
                .bio(user.getBio())
                .memberSince(user.getMemberSince())
                .build();
    }
    
    private SessionHistoryDto mapToSessionHistoryDto(Session session, SessionPlayer sessionPlayer) {
        return SessionHistoryDto.builder()
                .sessionId(session.getId())
                .sportType(session.getSportType())
                .title(session.getTitle())
                .date(session.getDate())
                .timeStart(session.getTimeStart())
                .timeEnd(session.getTimeEnd())
                .status(session.getStatus())
                .isHost(sessionPlayer.getIsHost())
                .joinedAt(sessionPlayer.getJoinedAt())
                .build();
    }
}

