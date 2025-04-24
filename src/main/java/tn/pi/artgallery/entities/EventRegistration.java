package tn.pi.artgallery.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_registration")
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "ticket_code")
    private String ticketCode;

    // Constructors
    public EventRegistration() {}

    public EventRegistration(Event event, User user, LocalDateTime registrationDate,
                             String paymentStatus, String paymentReference, String ticketCode) {
        this.event = event;
        this.user = user;
        this.registrationDate = registrationDate;
        this.paymentStatus = paymentStatus;
        this.paymentReference = paymentReference;
        this.ticketCode = ticketCode;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    // toString() for debugging
    @Override
    public String toString() {
        return "EventRegistration{" +
                "id=" + id +
                ", event=" + event +
                ", user=" + user +
                ", registrationDate=" + registrationDate +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}