// EventRepository.java
package tn.pi.artgallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.artgallery.entities.Event;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStartDateAfter(LocalDateTime date);
    List<Event> findByTitleContainingIgnoreCase(String title);
}