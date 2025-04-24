package tn.pi.artgallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.artgallery.entities.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}