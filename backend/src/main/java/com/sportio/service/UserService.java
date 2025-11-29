package com.sportio.service;

import com.sportio.entity.User;
import com.sportio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Flux<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public Mono<User> getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        return userRepository.findById(id);
    }

    public Mono<User> getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> userExistsByEmail(String email) {
        log.info("Checking if user exists by email: {}", email);
        return userRepository.existsByEmail(email);
    }

    public Mono<User> createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        return userRepository.save(user);
    }

    /**
     * Update an existing user.
     *
     * @param user the user entity with updated fields
     * @return Mono containing the updated user
     */
    public Mono<User> updateUser(User user) {
        log.info("Updating user with id: {}", user.getId());
        return userRepository.save(user);
    }
}