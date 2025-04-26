package tn.pi.artgallery.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event_payment")
public class EventPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private EventRegistration registration;

    private Double amount;
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    private Date paymentDate;
    private String paymentMethod; // CREDIT_CARD, PAYPAL, etc.
    private String transactionId;
    private String cardLastFour;

    public EventPayment() {
        this.paymentDate = new Date();
        this.status = "PENDING";
    }

    // Constructors, Getters and Setters
    public EventPayment(EventRegistration registration, Double amount, String status,
                        String paymentMethod, String transactionId, String cardLastFour) {
        this.registration = registration;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.cardLastFour = cardLastFour;
        this.paymentDate = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public EventRegistration getRegistration() { return registration; }
    public void setRegistration(EventRegistration registration) { this.registration = registration; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getCardLastFour() { return cardLastFour; }
    public void setCardLastFour(String cardLastFour) { this.cardLastFour = cardLastFour; }
}