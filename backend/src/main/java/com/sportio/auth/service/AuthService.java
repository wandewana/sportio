package com.sportio.auth.service;

import com.sportio.auth.dto.AuthResponse;
import com.sportio.auth.dto.LoginRequest;
import com.sportio.auth.dto.RegisterRequest;
import com.sportio.auth.dto.UserDto;
import com.sportio.auth.exception.EmailAlreadyExistsException;
import com.sportio.auth.exception.InvalidCredentialsException;
import com.sportio.auth.exception.PasswordMismatchException;
import com.sportio.auth.util.JwtUtil;
import com.sportio.entity.User;
import com.sportio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Service handling authentication business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user account.
     *
     * @param request the registration request containing user details
     * @return AuthResponse with JWT tokens and user info
     */
    public Mono<AuthResponse> register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password mismatch during registration for email: {}", request.getEmail());
            return Mono.error(new PasswordMismatchException());
        }

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        return userRepository.existsByEmail(normalizedEmail)
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Email already exists: {}", normalizedEmail);
                        return Mono.error(new EmailAlreadyExistsException(normalizedEmail));
                    }

                    // Create new user
                    User newUser = User.builder()
                            .email(normalizedEmail)
                            .passwordHash(passwordEncoder.encode(request.getPassword()))
                            .fullName(request.getFullName().trim())
                            .avatarInitials(generateInitials(request.getFullName()))
                            .skillLevel("Beginner")
                            .gamesPlayed(0)
                            .memberSince(LocalDateTime.now())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                })
                .map(this::buildAuthResponse);
    }

    /**
     * Authenticate user credentials and return JWT tokens.
     *
     * @param request the login request containing email and password
     * @return AuthResponse with JWT tokens and user info
     */
    public Mono<AuthResponse> login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.getEmail());

        String normalizedEmail = request.getEmail().toLowerCase().trim();

        return userRepository.findByEmail(normalizedEmail)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User not found for email: {}", normalizedEmail);
                    return Mono.error(new InvalidCredentialsException());
                }))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                        log.warn("Invalid password for email: {}", normalizedEmail);
                        return Mono.error(new InvalidCredentialsException());
                    }
                    return Mono.just(user);
                })
                .map(this::buildAuthResponse);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .avatarInitials(user.getAvatarInitials())
                .skillLevel(user.getSkillLevel())
                .gamesPlayed(user.getGamesPlayed())
                .bio(user.getBio())
                .memberSince(user.getMemberSince())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .user(userDto)
                .build();
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
}

