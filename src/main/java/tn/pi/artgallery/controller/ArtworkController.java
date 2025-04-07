package tn.pi.artgallery.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import tn.pi.artgallery.entities.Artist;
import tn.pi.artgallery.entities.Artwork;
import tn.pi.artgallery.entities.Payment;
import tn.pi.artgallery.services.ArtworkService;
import tn.pi.artgallery.services.ArtistService;
import tn.pi.artgallery.services.PaymentService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Path("/artworks")  // API Base Path
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtworkController {

    private final ArtworkService artworkService;
    private final ArtistService artistService;
    PaymentService  paymentService;
    @Autowired
    public ArtworkController(ArtworkService artworkService, ArtistService artistService) {
        this.artworkService = artworkService;
        this.artistService = artistService;
    }
    @GET
    public Response getAllArtworks() {
        List<Artwork> artworks = artworkService.getAllArtworks();

        if (artworks.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        // Convert artworks to include image URL
        List<Map<String, Object>> artworksWithImages = artworks.stream().map(artwork -> {
            Map<String, Object> artworkMap = new HashMap<>();
            artworkMap.put("id", artwork.getId());
            artworkMap.put("title", artwork.getTitle());
            artworkMap.put("description", artwork.getDescription());
            artworkMap.put("price", artwork.getPrice());
            artworkMap.put("artistName", artwork.getArtist().getName());
            artworkMap.put("imageUrl", "http://localhost:8080/artworks/" + artwork.getId() + "/image"); // URL pour récupérer l'image
            return artworkMap;
        }).toList();

        return Response.ok(artworksWithImages).build();
    }

    // ✅ Endpoint to upload an artwork with an image
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createArtworkWithImage(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("price") Double price,
            @FormDataParam("artistId") Long artistId) {

        try {
            // 🔴 Validate required fields
            if (artistId == null || title == null || fileDetail.getFileName() == null || description == null || price == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing required fields").build();
            }

            // 🔄 Convert uploaded image to byte[]
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = uploadedInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] imageBytes = buffer.toByteArray();

            // 🔍 Fetch the artist by ID
            Optional<Artist> artist = artistService.getArtistById(artistId);
            if (artist.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Artist not found").build();
            }

            // 🎨 Create and save the artwork
            Artwork artwork = new Artwork();
            artwork.setTitle(title);
            artwork.setDescription(description);
            artwork.setPrice(price);
            artwork.setArtist(artist.get());
            artwork.setImage(imageBytes);  // Save image as byte[]
            artwork.setCreationDate(LocalDate.now());  // Save current date

            // 💾 Save to database
            Artwork savedArtwork = artworkService.saveArtwork(artwork);

            return Response.status(Response.Status.CREATED)
                    .entity(savedArtwork).build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing image").build();
        }
    }

    // ✅ Endpoint to get the artwork image by ID (Fix for image display)
    @GET
    @Path("/{id}/image")
    @Produces("image/jpeg")
    public Response getImage(@PathParam("id") Long id) {
        Optional<Artwork> artwork = artworkService.getArtworkById(id);

        if (artwork.isPresent() && artwork.get().getImage() != null) {
            return Response.ok(artwork.get().getImage()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("Image not found").build();
    }

    // ✅ Delete artwork by ID
    @DELETE
    @Path("/{id}")
    public Response deleteArtwork(@PathParam("id") Long id) {
        artworkService.deleteArtwork(id);
        return Response.noContent().build();
    }

    // Simulate a simple payment
    @POST
    @Path("/{id}/buy")
    public Response buyArtwork(@PathParam("id") Long id) {
        Optional<Artwork> artworkOptional = artworkService.getArtworkById(id);

        if (artworkOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Artwork not found")
                    .build();
        }

        Artwork artwork = artworkOptional.get();

        // Simulate the payment
        Payment payment = new Payment();
        payment.setArtwork(artwork);
        payment.setAmount(artwork.getPrice());
        payment.setStatus("Completed");

        // Save the payment record

        paymentService.savePayment(payment);

        // Mark the artwork as sold
        artworkService.markAsSold(id);

        return Response.status(Response.Status.OK)
                .entity("Payment successful for artwork: " + artwork.getTitle())
                .build();
    }

    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableArtworks() {
        List<Artwork> artworks = artworkService.getAvailableArtworks();

        if(artworks.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        // Conversion pour inclure les URLs d'images
        List<Map<String, Object>> response = artworks.stream().map(artwork -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", artwork.getId());
            item.put("title", artwork.getTitle());
            item.put("price", artwork.getPrice());
            item.put("imageUrl", "/api/artworks/" + artwork.getId() + "/image");
            item.put("available", artwork.isAvailable());
            return item;
        }).collect(Collectors.toList());

        return Response.ok(response).build();
    }

}
