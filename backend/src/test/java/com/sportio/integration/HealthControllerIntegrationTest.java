package com.sportio.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HealthControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void healthCheck_ShouldReturnServiceStatus() {
        // Call real API endpoint via WebTestClient
        webTestClient.get()
                .uri("/api/v1/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.service").isEqualTo("Sportio Backend")
                .jsonPath("$.version").isEqualTo("1.0.0");
    }

    @Test
    void healthCheck_ShouldContainTimestamp() {
        // Verify complete response structure
        webTestClient.get()
                .uri("/api/v1/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.timestamp").exists();
    }
}