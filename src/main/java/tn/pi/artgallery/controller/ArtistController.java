package tn.pi.artgallery.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.pi.artgallery.entities.Artist;
import tn.pi.artgallery.services.ArtistService;

import java.util.List;
import java.util.Optional;

@Component
@Path("/artists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GET
    public List<Artist> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GET
    @Path("/{id}")
    public Response getArtistById(@PathParam("id") Long id) {
        Optional<Artist> artist = artistService.getArtistById(id);
        return artist.map(value -> Response.ok(value).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response createArtist(Artist artist) {
        Artist savedArtist = artistService.saveArtist(artist);
        return Response.status(Response.Status.CREATED).entity(savedArtist).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteArtist(@PathParam("id") Long id) {
        artistService.deleteArtist(id);
        return Response.noContent().build();
    }
}
