package com.sportio.repository;

import com.sportio.entity.SessionPlayer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for SessionPlayer entity operations.
 */
@Repository
public interface SessionPlayerRepository extends R2dbcRepository<SessionPlayer, Long> {
    
    /**
     * Find all session participations for a user.
     *
     * @param userId the user's ID
     * @return Flux of session player entries
     */
    Flux<SessionPlayer> findByUserId(Long userId);
    
    /**
     * Find all players in a specific session.
     *
     * @param sessionId the session's ID
     * @return Flux of session player entries
     */
    Flux<SessionPlayer> findBySessionId(Long sessionId);
    
    /**
     * Count total session participations for a user.
     *
     * @param userId the user's ID
     * @return Mono with the count
     */
    Mono<Long> countByUserId(Long userId);
    
    /**
     * Find a specific user's participation in a session.
     *
     * @param sessionId the session's ID
     * @param userId the user's ID
     * @return Mono of the session player entry if exists
     */
    Mono<SessionPlayer> findBySessionIdAndUserId(Long sessionId, Long userId);
}

