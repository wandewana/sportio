package com.sportio.integration;

import com.sportio.auth.dto.LoginRequest;
import com.sportio.auth.dto.RegisterRequest;
import com.sportio.entity.User;
import com.sportio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AuthController.
 * Tests complete request-response flow through real endpoints with real database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll().block();
    }

    // TC001-07: Successful User Registration
    @Test
    void testRegister_Success_ReturnsTokensAndUserInfo() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("John Doe")
                .email("john@sportio.com")
                .password("SecurePass123")
                .confirmPassword("SecurePass123")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.accessToken").exists()
                .jsonPath("$.refreshToken").exists()
                .jsonPath("$.expiresIn").exists()
                .jsonPath("$.user.email").isEqualTo("john@sportio.com")
                .jsonPath("$.user.fullName").isEqualTo("John Doe")
                .jsonPath("$.user.id").exists();

        // Verify user was created in database
        User savedUser = userRepository.findByEmail("john@sportio.com").block();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getFullName()).isEqualTo("John Doe");
        // Verify password was hashed (not stored as plain text)
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("SecurePass123");
        assertThat(passwordEncoder.matches("SecurePass123", savedUser.getPasswordHash())).isTrue();
    }

    // TC001-08: Registration with Mismatched Passwords
    @Test
    void testRegister_MismatchedPasswords_Returns400() {
        RegisterRequest request = RegisterRequest.builder()
                .fullName("John")
                .email("john@test.com")
                .password("pass1234")
                .confirmPassword("pass5678")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("password_mismatch")
                .jsonPath("$.message").isEqualTo("Passwords do not match");
    }

    // TC001-09: Registration with Existing Email
    @Test
    void testRegister_DuplicateEmail_Returns409() {
        // Create existing user
        User existingUser = User.builder()
                .email("existing@sportio.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Existing User")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(existingUser).block();

        RegisterRequest request = RegisterRequest.builder()
                .fullName("John")
                .email("existing@sportio.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterRequest.class)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.error").isEqualTo("email_exists");
    }

    // TC001-01: Successful Login with Valid Credentials
    @Test
    void testLogin_Success_ReturnsTokensAndUserInfo() {
        // Create user first
        User user = User.builder()
                .email("alex@sportio.com")
                .passwordHash(passwordEncoder.encode("ValidPassword123"))
                .fullName("Alex Smith")
                .skillLevel("Intermediate")
                .gamesPlayed(10)
                .memberSince(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user).block();

        LoginRequest request = LoginRequest.builder()
                .email("alex@sportio.com")
                .password("ValidPassword123")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accessToken").exists()
                .jsonPath("$.refreshToken").exists()
                .jsonPath("$.expiresIn").exists()
                .jsonPath("$.user.email").isEqualTo("alex@sportio.com")
                .jsonPath("$.user.fullName").isEqualTo("Alex Smith");
    }

    // TC001-02: Login with Invalid Email Format
    @Test
    void testLogin_InvalidEmailFormat_Returns400() {
        LoginRequest request = LoginRequest.builder()
                .email("invalidemail")
                .password("anypass")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("validation_error")
                .jsonPath("$.fieldErrors.email").exists();
    }

    // TC001-03: Login with Empty Credentials
    @Test
    void testLogin_EmptyCredentials_Returns400() {
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("validation_error");
    }

    // TC001-04: Login with Incorrect Password
    @Test
    void testLogin_WrongPassword_Returns401() {
        // Create user first
        User user = User.builder()
                .email("test@sportio.com")
                .passwordHash(passwordEncoder.encode("correctpassword"))
                .fullName("Test User")
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user).block();

        LoginRequest request = LoginRequest.builder()
                .email("test@sportio.com")
                .password("wrongpassword")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("invalid_credentials");
    }

    // Additional test: Login with non-existent user
    @Test
    void testLogin_UserNotFound_Returns401() {
        LoginRequest request = LoginRequest.builder()
                .email("nonexistent@sportio.com")
                .password("anypassword")
                .build();

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), LoginRequest.class)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("invalid_credentials");
    }
}

