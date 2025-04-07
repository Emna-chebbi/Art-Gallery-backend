package tn.pi.artgallery.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "artworks")
public class Artwork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double price;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB") // Utilisez LONGBLOB pour MySQL
    private byte[] image;

    // ✅ Prevent infinite recursion when serializing JSON
    @ManyToOne(fetch = FetchType.EAGER) // ⚠️ Not recommended for large datasets
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
    @Column(nullable = false)
    private Boolean available = true;

    public Boolean isAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    // ✅ Constructors
    public Artwork() {}

    public Artwork(String title, String description, Double price, LocalDate creationDate, Artist artist, byte[] image) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.creationDate = creationDate;
        this.artist = artist;
        this.image = image;
    }

    // ✅ Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
