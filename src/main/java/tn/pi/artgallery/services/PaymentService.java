package tn.pi.artgallery.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.pi.artgallery.entities.Payment;
import tn.pi.artgallery.repository.PaymentRepository;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> findByUserId(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> findByUserIdOrderByDateDesc(Long userId) {
        return paymentRepository.findByUserIdOrderByDateDesc(userId);
    }

    // New method for admin - get all payments with pagination
    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAllByOrderByDateDesc(pageable);
    }

    // New method for admin - search payments
    public Page<Payment> searchPayments(String query, Pageable pageable) {
        return paymentRepository.searchPayments(query, pageable);
    }
    public long getTransactionsCount() {
        return paymentRepository.count();
    }
}