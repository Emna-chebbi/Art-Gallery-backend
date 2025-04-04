package tn.pi.artgallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.artgallery.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}