package com.imshawan.rest.service;

import com.imshawan.rest.model.User;
import com.imshawan.rest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isUsernameOrEmailTaken(String username, String email) {
        return userRepository.findByUsername(username).isPresent() ||
               userRepository.findByEmail(email).isPresent();
    }

    public User registerUser(User user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> updateUserById(String id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFullname(updatedUser.getFullname());
            existingUser.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(existingUser);
        });
    }

    public boolean deleteUserById(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> authenticateByEmail(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPasswordHash())) {
            return user;
        }

        return Optional.empty();
    }

    public Optional<User> authenticateByUsername(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPasswordHash())) {
            return user;
        }

        return Optional.empty();
    }
}
