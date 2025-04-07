package tn.pi.artgallery.services;

import org.springframework.stereotype.Service;
import tn.pi.artgallery.entities.Payment;
import tn.pi.artgallery.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Save the payment in the database
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
