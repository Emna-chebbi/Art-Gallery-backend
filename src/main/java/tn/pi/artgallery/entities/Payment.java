package tn.pi.artgallery.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name="payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Artwork artwork;

    @ManyToOne
    private User user;

    private Double amount;
    private String status;
    private Date date;

    public Payment() {}

    public Payment(Artwork artwork, User user, Double amount, String status) {
        this.artwork = artwork;
        this.user = user;
        this.amount = amount;
        this.status = status;
        this.date = new Date();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public User getUser() {
        return user;
    }

    public Double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}