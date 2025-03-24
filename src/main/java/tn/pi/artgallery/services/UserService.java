package tn.pi.artgallery.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.pi.artgallery.entities.User;
import tn.pi.artgallery.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void register(User user) {
        // Sauvegarde directe sans chiffrement
        userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        // Vérification simple du mot de passe en clair
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}