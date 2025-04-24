package tn.pi.artgallery.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ArtistRepository artistRepository;

    public ArtworkService(ArtworkRepository artworkRepository, ArtistRepository artistRepository) {
        this.artworkRepository = artworkRepository;
        this.artistRepository = artistRepository;
    }

    // Updated to support pagination
    public Page<Artwork> getAllArtworks(Pageable pageable) {
        return artworkRepository.findAll(pageable);
    }

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

    @Transactional
    public void deleteArtwork(Long id) {
        artworkRepository.deleteById(id);
    }

    // Updated to support pagination
    public Page<Artwork> getAvailableArtworks(Pageable pageable) {
        return artworkRepository.findByAvailable(true, pageable);
    }

    // Updated to support pagination
    public Page<Artwork> getSoldArtworks(Pageable pageable) {
        return artworkRepository.findByAvailable(false, pageable);
    }

    @Transactional
    public Artwork toggleAvailability(Long id) {
        Artwork artwork = artworkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artwork not found"));
        artwork.setAvailable(!artwork.isAvailable());
        return artworkRepository.save(artwork);
    }
    public long getArtworksCount() {
        return artworkRepository.count();
    }
}