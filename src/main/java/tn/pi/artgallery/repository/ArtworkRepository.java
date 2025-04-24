package tn.pi.artgallery.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.pi.artgallery.entities.Artwork;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {
    Page<Artwork> findByAvailable(boolean available, Pageable pageable);
}