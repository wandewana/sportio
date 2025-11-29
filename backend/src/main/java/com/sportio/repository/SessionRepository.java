package com.sportio.repository;

import com.sportio.entity.Session;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository for Session entity operations.
 */
@Repository
public interface SessionRepository extends R2dbcRepository<Session, Long> {
    
    /**
     * Find all sessions hosted by a specific user.
     *
     * @param hostId the host user's ID
     * @return Flux of sessions hosted by the user
     */
    Flux<Session> findByHostId(Long hostId);
    
    /**
     * Find all sessions with a specific status.
     *
     * @param status the session status
     * @return Flux of sessions with the given status
     */
    Flux<Session> findByStatus(String status);
}

