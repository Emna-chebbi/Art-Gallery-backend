// EventRegistrationRepository.java
package tn.pi.artgallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.artgallery.entities.EventRegistration;
import tn.pi.artgallery.entities.User;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByUserId(Long userId);
    List<EventRegistration> findByEventId(Long eventId);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}