package tn.pi.artgallery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.pi.artgallery.entities.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByUserIdOrderByDateDesc(Long userId);

    Page<Payment> findAllByOrderByDateDesc(Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE " +
            "LOWER(p.artwork.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.status) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Payment> searchPayments(String query, Pageable pageable);
}