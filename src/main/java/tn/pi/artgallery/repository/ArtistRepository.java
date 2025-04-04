package tn.pi.artgallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.pi.artgallery.entities.Artist;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    @Query("SELECT a FROM Artist a LEFT JOIN FETCH a.artworks")
    List<Artist> findAllWithArtworks();
}
