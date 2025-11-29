package com.sportio.config;

import com.sportio.util.JwtUtil;
import com.sportio.entity.User;
import com.sportio.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * WebFilter that extracts and validates JWT tokens from Authorization header
 * and sets the authenticated user in the exchange attributes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    public static final String AUTHENTICATED_USER_KEY = "AUTHENTICATED_USER";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            String email = jwtUtil.extractEmail(token);
            log.debug("Extracted email from JWT: {}", email);

            return userRepository.findByEmail(email)
                    .flatMap(user -> {
                        log.debug("Authenticated user: {} (id: {})", user.getEmail(), user.getId());
                        // Store user in exchange attributes for access in controllers
                        exchange.getAttributes().put(AUTHENTICATED_USER_KEY, user);
                        return chain.filter(exchange);
                    })
                    // Use Mono.defer to lazily evaluate chain.filter - prevents double execution
                    .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));

        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            return chain.filter(exchange);
        }
    }

    /**
     * Helper method to extract authenticated user from reactive context.
     * Uses Mono.deferContextual to access the context set by the filter.
     *
     * @return Mono containing the authenticated user, or empty if not authenticated
     */
    public static Mono<User> getAuthenticatedUser() {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(AUTHENTICATED_USER_KEY)) {
                return Mono.just(ctx.get(AUTHENTICATED_USER_KEY));
            }
            return Mono.empty();
        });
    }

    /**
     * Helper method to extract authenticated user from exchange attributes.
     *
     * @param exchange the server web exchange
     * @return Mono containing the authenticated user, or empty if not authenticated
     */
    public static Mono<User> getAuthenticatedUser(ServerWebExchange exchange) {
        User user = exchange.getAttribute(AUTHENTICATED_USER_KEY);
        return user != null ? Mono.just(user) : Mono.empty();
    }
}

