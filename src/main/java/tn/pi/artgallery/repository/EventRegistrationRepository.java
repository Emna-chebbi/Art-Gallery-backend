package tn.pi.artgallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.artgallery.entities.EventRegistration;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);
    List<EventRegistration> findByUserId(Long userId);
    List<EventRegistration> findByEventId(Long eventId);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}