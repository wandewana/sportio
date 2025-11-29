package com.sportio.controller;

import com.sportio.config.JwtAuthenticationFilter;
import com.sportio.dto.*;
import com.sportio.exception.UnauthorizedException;
import com.sportio.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for user profile management endpoints.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Get current authenticated user's full profile with stats.
     *
     * @param exchange the server web exchange
     * @return the user's full profile including statistics
     */
    @GetMapping("/me")
    public Mono<UserProfileDto> getMyProfile(ServerWebExchange exchange) {
        return JwtAuthenticationFilter.getAuthenticatedUser(exchange)
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMap(user -> {
                    log.info("Getting profile for authenticated user: {}", user.getId());
                    return profileService.getFullProfile(user.getId());
                });
    }

    /**
     * Update current authenticated user's profile.
     *
     * @param exchange the server web exchange
     * @param request the profile update request
     * @return the updated user profile
     */
    @PutMapping("/me")
    public Mono<UserProfileDto> updateMyProfile(
            ServerWebExchange exchange,
            @Valid @RequestBody UserUpdateRequest request) {
        return JwtAuthenticationFilter.getAuthenticatedUser(exchange)
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMap(user -> {
                    log.info("Updating profile for authenticated user: {}", user.getId());
                    return profileService.updateProfile(user.getId(), request);
                });
    }

    /**
     * Get public profile of another user.
     *
     * @param userId the user's ID
     * @return the user's public profile (excludes email)
     */
    @GetMapping("/{userId}")
    public Mono<UserPublicProfileDto> getPublicProfile(@PathVariable Long userId) {
        log.info("Getting public profile for user: {}", userId);
        return profileService.getPublicProfile(userId);
    }

    /**
     * Get current authenticated user's session history.
     *
     * @param exchange the server web exchange
     * @return list of session history entries
     */
    @GetMapping("/me/sessions")
    public Flux<SessionHistoryDto> getMySessions(ServerWebExchange exchange) {
        return JwtAuthenticationFilter.getAuthenticatedUser(exchange)
                .switchIfEmpty(Mono.error(new UnauthorizedException()))
                .flatMapMany(user -> {
                    log.info("Getting session history for authenticated user: {}", user.getId());
                    return profileService.getSessionHistory(user.getId());
                });
    }
}

