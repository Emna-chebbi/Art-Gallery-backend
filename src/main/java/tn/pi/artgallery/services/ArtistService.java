package tn.pi.artgallery.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.pi.artgallery.entities.Artist;
import tn.pi.artgallery.repository.ArtistRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }
    @Transactional(readOnly = true)
    public List<Artist> getAllArtists() {
        return artistRepository.findAllWithArtworks();
    }

    public Optional<Artist> getArtistById(Long id) {
        return artistRepository.findById(id);
    }

    @Transactional
    public Artist saveArtist(Artist artist) {
        return artistRepository.save(artist);
    }

    @Transactional
    public void deleteArtist(Long id) {
        artistRepository.deleteById(id);
    }
}
