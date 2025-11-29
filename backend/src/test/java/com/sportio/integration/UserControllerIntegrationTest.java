package com.sportio.integration;

import com.sportio.entity.User;
import com.sportio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        userRepository.deleteAll().block();
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
        // Call real API endpoint
        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(0);
    }

    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Prepare test data
        User testUser = User.builder()
                .email("test@example.com")
                .passwordHash("hashed_password")
                .fullName("Test User")
                .skillLevel("Intermediate")
                .gamesPlayed(5)
                .memberSince(LocalDateTime.now())
                .build();

        // Call real API endpoint
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(testUser), User.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.fullName").isEqualTo("Test User")
                .jsonPath("$.id").exists();

        // Verify database state changed correctly
        User savedUser = userRepository.findByEmail("test@example.com").block();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getFullName()).isEqualTo("Test User");
        assertThat(savedUser.getSkillLevel()).isEqualTo("Intermediate");
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Insert test data into real database
        User testUser = User.builder()
                .email("test2@example.com")
                .passwordHash("hashed_password")
                .fullName("Test User 2")
                .build();
        User savedUser = userRepository.save(testUser).block();

        // Call real API endpoint - returns public profile (email is excluded for privacy)
        webTestClient.get()
                .uri("/api/v1/users/{id}", savedUser.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(savedUser.getId())
                .jsonPath("$.fullName").isEqualTo("Test User 2")
                .jsonPath("$.email").doesNotExist(); // Public profile does not expose email
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturn404() {
        // Call real API endpoint with non-existent ID
        webTestClient.get()
                .uri("/api/v1/users/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Insert test data into real database
        User testUser = User.builder()
                .email("test3@example.com")
                .passwordHash("hashed_password")
                .fullName("Test User 3")
                .build();
        userRepository.save(testUser).block();

        // Call real API endpoint
        webTestClient.get()
                .uri("/api/v1/users/email/{email}", "test3@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("test3@example.com")
                .jsonPath("$.fullName").isEqualTo("Test User 3");
    }

    @Test
    void getUserByEmail_WhenUserNotFound_ShouldReturn404() {
        // Call real API endpoint with non-existent email
        webTestClient.get()
                .uri("/api/v1/users/email/{email}", "nonexistent@example.com")
                .exchange()
                .expectStatus().isNotFound();
    }
}