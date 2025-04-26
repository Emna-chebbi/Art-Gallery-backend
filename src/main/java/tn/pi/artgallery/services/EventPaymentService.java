package tn.pi.artgallery.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.pi.artgallery.entities.EventPayment;
import tn.pi.artgallery.entities.EventRegistration;
import tn.pi.artgallery.repository.EventPaymentRepository;
import tn.pi.artgallery.repository.EventRegistrationRepository;

import java.util.List;

@Service
public class EventPaymentService {
    private final EventPaymentRepository paymentRepository;
    private final EventRegistrationRepository registrationRepository;

    @Autowired
    public EventPaymentService(EventPaymentRepository paymentRepository,
                               EventRegistrationRepository registrationRepository) {
        this.paymentRepository = paymentRepository;
        this.registrationRepository = registrationRepository;
    }

    public EventPayment processPayment(Long registrationId, String paymentMethod,
                                       String cardLastFour, String transactionId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        EventPayment payment = new EventPayment();
        payment.setRegistration(registration);
        payment.setAmount(registration.getEvent().getPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setCardLastFour(cardLastFour);
        payment.setTransactionId(transactionId);
        payment.setStatus("COMPLETED");

        // Update registration status
        registration.setPaymentStatus("PAID");
        registrationRepository.save(registration);

        return paymentRepository.save(payment);
    }

    public List<EventPayment> getUserEventPayments(Long userId) {
        return paymentRepository.findByRegistrationUserId(userId);
    }

    public List<EventPayment> getUserEventPaymentHistory(Long userId) {
        return paymentRepository.findByRegistrationUserIdOrderByPaymentDateDesc(userId);
    }

    public Page<EventPayment> getAllEventPayments(Pageable pageable) {
        return paymentRepository.findAllByOrderByPaymentDateDesc(pageable);
    }

    public Page<EventPayment> searchEventPayments(String query, Pageable pageable) {
        return paymentRepository.searchEventPayments(query, pageable);
    }

    public long getEventTransactionsCount() {
        return paymentRepository.count();
    }
}