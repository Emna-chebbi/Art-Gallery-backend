package tn.pi.artgallery.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tn.pi.artgallery.entities.User;
import tn.pi.artgallery.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        // Validate email uniqueness
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Encode password before saving
        user.setPassword((user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Additional useful methods
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    public User updateUser(Long userId, User userDetails) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (userDetails.getFullName() != null) {
                        user.setFullName(userDetails.getFullName());
                    }
                    if (userDetails.getEmail() != null) {
                        user.setEmail(userDetails.getEmail());
                    }
                    if (userDetails.getPhone() != null) {
                        user.setPhone(userDetails.getPhone());
                    }
                    if (userDetails.getAddress() != null) {
                        user.setAddress(userDetails.getAddress());
                    }
                    if (userDetails.getImageUrl() != null) {
                        user.setImageUrl(userDetails.getImageUrl());
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    public List<User> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page - 1, size)).getContent();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public User createUser(User user) {
        // Ici vous devriez hasher le mot de passe avant de sauvegarder
        return userRepository.save(user);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public long getUsersCount() {
        return userRepository.count();
    }
}