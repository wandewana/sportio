package com.sportio.integration;

import com.sportio.util.JwtUtil;
import com.sportio.dto.UserUpdateRequest;
import com.sportio.entity.Session;
import com.sportio.entity.SessionPlayer;
import com.sportio.entity.User;
import com.sportio.repository.SessionPlayerRepository;
import com.sportio.repository.SessionRepository;
import com.sportio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ProfileController.
 * Tests complete request-response flow through real endpoints with real database.
 *
 * Uses a manually configured WebTestClient to avoid issues with Spring Security's
 * ReactorContextTestExecutionListener interfering with custom JWT authentication.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProfileControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionPlayerRepository sessionPlayerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Create WebTestClient manually to avoid Spring Security's ReactorContextTestExecutionListener
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(30))
                .build();

        // Clean database before each test
        sessionPlayerRepository.deleteAll().block();
        sessionRepository.deleteAll().block();
        userRepository.deleteAll().block();

        // Create test user
        testUser = insertTestUser();
        validToken = generateTestToken(testUser);
    }

    // TC012-01: Display Profile Screen - Get authenticated user's profile
    @Test
    void testGetMyProfile_Authenticated_ReturnsProfile() {
        webTestClient.get()
                .uri("/api/v1/users/me")
                .header("Authorization", "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(testUser.getId())
                .jsonPath("$.email").isEqualTo(testUser.getEmail())
                .jsonPath("$.fullName").isEqualTo(testUser.getFullName())
                .jsonPath("$.skillLevel").isEqualTo(testUser.getSkillLevel())
                .jsonPath("$.avatarInitials").isEqualTo(testUser.getAvatarInitials())
                .jsonPath("$.stats").exists()
                .jsonPath("$.stats.totalBookings").isEqualTo(0);
    }

    @Test
    void testGetMyProfile_NoAuth_Returns401() {
        webTestClient.get()
                .uri("/api/v1/users/me")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("unauthorized");
    }

    @Test
    void testGetMyProfile_InvalidToken_Returns401() {
        webTestClient.get()
                .uri("/api/v1/users/me")
                .header("Authorization", "Bearer invalid.token.here")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo("unauthorized");
    }

    // TC012-04: Display Statistics Grid - Verify stats calculation
    @Test
    void testGetMyProfile_WithSessions_ReturnsCorrectStats() {
        // Create a session and join user to it
        Session session = insertTestSession(testUser);
        joinSession(testUser, session);

        webTestClient.get()
                .uri("/api/v1/users/me")
                .header("Authorization", "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stats.totalBookings").isEqualTo(1);
    }

    // TC012-06: Save Profile Changes
    @Test
    void testUpdateMyProfile_ValidRequest_Returns200() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("Updated Name");
        request.setSkillLevel("Advanced");
        request.setBio("I love sports!");

        webTestClient.put()
                .uri("/api/v1/users/me")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserUpdateRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.fullName").isEqualTo("Updated Name")
                .jsonPath("$.skillLevel").isEqualTo("Advanced")
                .jsonPath("$.bio").isEqualTo("I love sports!")
                .jsonPath("$.avatarInitials").isEqualTo("UN"); // Updated initials

        // Verify database was updated
        User updatedUser = userRepository.findById(testUser.getId()).block();
        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getSkillLevel()).isEqualTo("Advanced");
        assertThat(updatedUser.getBio()).isEqualTo("I love sports!");
    }

    @Test
    void testUpdateMyProfile_InvalidSkillLevel_Returns400() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setSkillLevel("InvalidLevel");

        webTestClient.put()
                .uri("/api/v1/users/me")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserUpdateRequest.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("validation_error");
    }

    @Test
    void testUpdateMyProfile_NoAuth_Returns401() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFullName("New Name");

        webTestClient.put()
                .uri("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), UserUpdateRequest.class)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // Get public profile of another user
    @Test
    void testGetPublicProfile_ExistingUser_ReturnsProfile() {
        User otherUser = User.builder()
                .email("other@sportio.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Other User")
                .avatarInitials("OU")
                .skillLevel("Beginner")
                .gamesPlayed(5)
                .memberSince(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        otherUser = userRepository.save(otherUser).block();

        webTestClient.get()
                .uri("/api/v1/users/" + otherUser.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(otherUser.getId())
                .jsonPath("$.fullName").isEqualTo("Other User")
                .jsonPath("$.skillLevel").isEqualTo("Beginner")
                // Email should NOT be present in public profile
                .jsonPath("$.email").doesNotExist();
    }

    @Test
    void testGetPublicProfile_NonExistentUser_Returns404() {
        webTestClient.get()
                .uri("/api/v1/users/999999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("user_not_found");
    }

    // Session history tests
    @Test
    void testGetMySessions_Authenticated_ReturnsList() {
        Session session = insertTestSession(testUser);
        joinSession(testUser, session);

        webTestClient.get()
                .uri("/api/v1/users/me/sessions")
                .header("Authorization", "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].sessionId").isEqualTo(session.getId())
                .jsonPath("$[0].sportType").isEqualTo("Badminton")
                .jsonPath("$[0].title").isEqualTo("Test Session");
    }

    @Test
    void testGetMySessions_NoSessions_ReturnsEmptyList() {
        webTestClient.get()
                .uri("/api/v1/users/me/sessions")
                .header("Authorization", "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }

    @Test
    void testGetMySessions_NoAuth_Returns401() {
        webTestClient.get()
                .uri("/api/v1/users/me/sessions")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // Helper methods

    private User insertTestUser() {
        User user = User.builder()
                .email("test@sportio.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .avatarInitials("TU")
                .skillLevel("Intermediate")
                .gamesPlayed(10)
                .memberSince(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return userRepository.save(user).block();
    }

    private String generateTestToken(User user) {
        return jwtUtil.generateAccessToken(user);
    }

    private Session insertTestSession(User host) {
        Session session = Session.builder()
                .hostId(host.getId())
                .sportType("Badminton")
                .title("Test Session")
                .description("A test session")
                .date(LocalDate.now().plusDays(1))
                .timeStart(LocalTime.of(19, 0))
                .timeEnd(LocalTime.of(21, 0))
                .playersNeeded(4)
                .visibility("public")
                .status("open")
                .latitude(BigDecimal.valueOf(1.3521))
                .longitude(BigDecimal.valueOf(103.8198))
                .createdAt(LocalDateTime.now())
                .build();
        return sessionRepository.save(session).block();
    }

    private SessionPlayer joinSession(User user, Session session) {
        SessionPlayer sessionPlayer = SessionPlayer.builder()
                .sessionId(session.getId())
                .userId(user.getId())
                .isHost(session.getHostId().equals(user.getId()))
                .status("joined")
                .joinedAt(LocalDateTime.now())
                .build();
        return sessionPlayerRepository.save(sessionPlayer).block();
    }
}

