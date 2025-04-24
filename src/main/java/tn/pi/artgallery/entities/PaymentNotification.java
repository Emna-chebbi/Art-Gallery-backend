package tn.pi.artgallery.entities;

import java.util.Date;

public class PaymentNotification {
    private Long paymentId;
    private String message;
    private Double amount;
    private Date date;
    private String cardLastFour;
    private String artworkTitle;

    // Constructeur
    public PaymentNotification(Long paymentId, String message, Double amount, Date date, String cardLastFour) {
        this.paymentId = paymentId;
        this.message = message;
        this.amount = amount;
        this.date = date;
        this.cardLastFour = cardLastFour;
        this.artworkTitle = artworkTitle;

    }

    public Long getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getCardLastFour() {

        return cardLastFour;
    }
    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }
    public String getArtworkTitle() {
        return artworkTitle;
    }
    public void setArtworkTitle(String artworkTitle) {
        this.artworkTitle = artworkTitle;
    }

}
