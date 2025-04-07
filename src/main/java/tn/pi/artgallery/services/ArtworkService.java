package tn.pi.artgallery.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pi.artgallery.entities.Artwork;
import tn.pi.artgallery.entities.Artist;
import tn.pi.artgallery.repository.ArtworkRepository;
import tn.pi.artgallery.repository.ArtistRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtistRepository artistRepository; // ✅ Inject ArtistRepository

    public ArtworkService(ArtworkRepository artworkRepository, ArtistRepository artistRepository) {
        this.artworkRepository = artworkRepository;
        this.artistRepository = artistRepository;
    }

    // ✅ Retrieve all artworks
    public List<Artwork> getAllArtworks() {
        return artworkRepository.findAll();
    }

    // ✅ Retrieve an artwork by ID
    public Optional<Artwork> getArtworkById(Long id) {
        return artworkRepository.findById(id);
    }

    @Transactional
    public Artwork saveArtwork(Artwork artwork) {
        if (artwork.getArtist() == null || artwork.getArtist().getId() == null) {
            throw new IllegalArgumentException("Artist ID is required");
        }

        Artist artist = artistRepository.findById(artwork.getArtist().getId())
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));

        if (artwork.getCreationDate() == null) {
            artwork.setCreationDate(LocalDate.now());
        }

        return artworkRepository.save(artwork);
    }
    // ✅ Delete an artwork by ID
    @Transactional
    public void deleteArtwork(Long id) {
        artworkRepository.deleteById(id);
    }
    public List<Artwork> getAvailableArtworks() {
        return artworkRepository.findByAvailableTrue();
    }

    @Transactional
    public void markAsSold(Long id) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artwork not found with id: " + id));

        if (!artwork.isAvailable()) {
            throw new IllegalArgumentException("Artwork already marked as sold");
        }

        artwork.setAvailable(false);
        artworkRepository.save(artwork);
    }


}
