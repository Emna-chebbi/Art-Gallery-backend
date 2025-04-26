package tn.pi.artgallery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.pi.artgallery.entities.EventPayment;

import java.util.List;

public interface EventPaymentRepository extends JpaRepository<EventPayment, Long> {
    List<EventPayment> findByRegistrationUserId(Long userId);
    List<EventPayment> findByRegistrationUserIdOrderByPaymentDateDesc(Long userId);
    Page<EventPayment> findAllByOrderByPaymentDateDesc(Pageable pageable);

    @Query("SELECT ep FROM EventPayment ep WHERE " +
            "LOWER(ep.registration.event.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ep.registration.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(ep.status) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<EventPayment> searchEventPayments(String query, Pageable pageable);
}