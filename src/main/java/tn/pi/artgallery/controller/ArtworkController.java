package tn.pi.artgallery.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import tn.pi.artgallery.entities.*;
import tn.pi.artgallery.services.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Path("/artworks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtworkController {

    private final ArtworkService artworkService;
    private final ArtistService artistService;
    private final PaymentService paymentService;
    private final UserService userService;

    @Autowired
    public ArtworkController(ArtworkService artworkService,
                             ArtistService artistService,
                             PaymentService paymentService,
                             UserService userService) {
        this.artworkService = artworkService;
        this.artistService = artistService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @GET
    public Response getAllArtworks(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Artwork> artworkPage = artworkService.getAllArtworks(pageable);

        if (artworkPage.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        List<Map<String, Object>> artworksWithImages = artworkPage.getContent().stream().map(artwork -> {
            Map<String, Object> artworkMap = new HashMap<>();
            artworkMap.put("id", artwork.getId());
            artworkMap.put("title", artwork.getTitle());
            artworkMap.put("description", artwork.getDescription());
            artworkMap.put("price", artwork.getPrice());
            artworkMap.put("artistName", artwork.getArtist().getName());
            artworkMap.put("artistId", artwork.getArtist().getId());
            artworkMap.put("imageUrl", "http://localhost:8080/artworks/" + artwork.getId() + "/image");
            artworkMap.put("available", artwork.isAvailable());
            return artworkMap;
        }).toList();

        Map<String, Object> response = new HashMap<>();
        response.put("artworks", artworksWithImages);
        response.put("total", artworkPage.getTotalElements());
        response.put("page", artworkPage.getNumber());
        response.put("pageSize", artworkPage.getSize());

        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    public Response getArtworkById(@PathParam("id") Long id) {
        Optional<Artwork> artwork = artworkService.getArtworkById(id);

        if (artwork.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Map<String, Object> artworkMap = new HashMap<>();
        artworkMap.put("id", artwork.get().getId());
        artworkMap.put("title", artwork.get().getTitle());
        artworkMap.put("description", artwork.get().getDescription());
        artworkMap.put("price", artwork.get().getPrice());
        artworkMap.put("artistName", artwork.get().getArtist().getName());
        artworkMap.put("artistId", artwork.get().getArtist().getId());
        artworkMap.put("imageUrl", "http://localhost:8080/artworks/" + artwork.get().getId() + "/image");
        artworkMap.put("available", artwork.get().isAvailable());

        return Response.ok(artworkMap).build();
    }

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

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            if (artistId == null || title == null || fileDetail.getFileName() == null ||
                    description == null || price == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Missing required fields").build();
            }

            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = uploadedInputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] imageBytes = buffer.toByteArray();

            Optional<Artist> artist = artistService.getArtistById(artistId);
            if (artist.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Artist not found").build();
            }

            Artwork artwork = new Artwork();
            artwork.setTitle(title);
            artwork.setDescription(description);
            artwork.setPrice(price);
            artwork.setArtist(artist.get());
            artwork.setImage(imageBytes);
            artwork.setCreationDate(LocalDate.now());
            artwork.setAvailable(true);

            Artwork savedArtwork = artworkService.saveArtwork(artwork);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedArtwork.getId());
            response.put("title", savedArtwork.getTitle());
            response.put("description", savedArtwork.getDescription());
            response.put("price", savedArtwork.getPrice());
            response.put("artistName", savedArtwork.getArtist().getName());
            response.put("imageUrl", "http://localhost:8080/artworks/" + savedArtwork.getId() + "/image");
            response.put("available", savedArtwork.isAvailable());

            return Response.status(Response.Status.CREATED)
                    .entity(response).build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing image").build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateArtwork(@PathParam("id") Long id, Map<String, Object> updates) {
        Optional<Artwork> artworkOptional = artworkService.getArtworkById(id);
        if (artworkOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Artwork artwork = artworkOptional.get();

        if (updates.containsKey("title")) {
            artwork.setTitle((String) updates.get("title"));
        }
        if (updates.containsKey("description")) {
            artwork.setDescription((String) updates.get("description"));
        }
        if (updates.containsKey("price")) {
            artwork.setPrice(Double.valueOf(updates.get("price").toString()));
        }
        if (updates.containsKey("artistId")) {
            Long artistId = Long.valueOf(updates.get("artistId").toString());
            Optional<Artist> artist = artistService.getArtistById(artistId);
            if (artist.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Artist not found").build();
            }
            artwork.setArtist(artist.get());
        }
        if (updates.containsKey("available")) {
            artwork.setAvailable(Boolean.valueOf(updates.get("available").toString()));
        }

        Artwork updatedArtwork = artworkService.saveArtwork(artwork);

        Map<String, Object> response = new HashMap<>();
        response.put("id", updatedArtwork.getId());
        response.put("title", updatedArtwork.getTitle());
        response.put("description", updatedArtwork.getDescription());
        response.put("price", updatedArtwork.getPrice());
        response.put("artistName", updatedArtwork.getArtist().getName());
        response.put("imageUrl", "http://localhost:8080/artworks/" + updatedArtwork.getId() + "/image");
        response.put("available", updatedArtwork.isAvailable());

        return Response.ok(response).build();
    }

    @PATCH
    @Path("/{id}/availability")
    public Response toggleAvailability(@PathParam("id") Long id) {
        Optional<Artwork> artworkOptional = artworkService.getArtworkById(id);
        if (artworkOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Artwork artwork = artworkOptional.get();
        artwork.setAvailable(!artwork.isAvailable());
        Artwork updatedArtwork = artworkService.saveArtwork(artwork);

        return Response.ok()
                .entity(Map.of(
                        "id", updatedArtwork.getId(),
                        "available", updatedArtwork.isAvailable()
                ))
                .build();
    }

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

    @DELETE
    @Path("/{id}")
    public Response deleteArtwork(@PathParam("id") Long id) {
        artworkService.deleteArtwork(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/buy")
    public Response buyArtwork(@PathParam("id") Long id, @Context HttpServletRequest request) {
        Optional<Artwork> artworkOptional = artworkService.getArtworkById(id);
        if (artworkOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Artwork not found")
                    .build();
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not logged in")
                    .build();
        }

        Long userId = (Long) session.getAttribute("userId");
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        Artwork artwork = artworkOptional.get();
        if (!artwork.isAvailable()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Artwork already sold")
                    .build();
        }

        User user = userOptional.get();

        Payment payment = new Payment();
        payment.setArtwork(artwork);
        payment.setUser(user);
        payment.setAmount(artwork.getPrice());
        payment.setStatus("Completed");
        payment.setDate(new Date());

        paymentService.savePayment(payment);

        artwork.setAvailable(false);
        artworkService.saveArtwork(artwork);

        return Response.ok()
                .entity(Map.of(
                        "message", "Payment successful",
                        "artwork", artwork.getTitle(),
                        "amount", artwork.getPrice(),
                        "date", payment.getDate()
                ))
                .build();
    }

    @GET
    @Path("/available")
    public Response getAvailableArtworks(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Artwork> artworkPage = artworkService.getAvailableArtworks(pageable);

        List<Map<String, Object>> response = artworkPage.getContent().stream()
                .map(artwork -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", artwork.getId());
                    item.put("title", artwork.getTitle());
                    item.put("description", artwork.getDescription());
                    item.put("price", artwork.getPrice());
                    item.put("imageUrl", "/artworks/" + artwork.getId() + "/image");
                    item.put("available", artwork.isAvailable());
                    item.put("artistName", artwork.getArtist().getName());
                    item.put("artistId", artwork.getArtist().getId());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> paginatedResponse = new HashMap<>();
        paginatedResponse.put("artworks", response);
        paginatedResponse.put("total", artworkPage.getTotalElements());
        paginatedResponse.put("page", artworkPage.getNumber());
        paginatedResponse.put("pageSize", artworkPage.getSize());

        return Response.ok(paginatedResponse).build();
    }

    @GET
    @Path("/sold")
    public Response getSoldArtworks(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Artwork> artworkPage = artworkService.getSoldArtworks(pageable);

        List<Map<String, Object>> response = artworkPage.getContent().stream()
                .map(artwork -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", artwork.getId());
                    item.put("title", artwork.getTitle());
                    item.put("description", artwork.getDescription());
                    item.put("price", artwork.getPrice());
                    item.put("imageUrl", "/artworks/" + artwork.getId() + "/image");
                    item.put("available", artwork.isAvailable());
                    item.put("artistName", artwork.getArtist().getName());
                    item.put("artistId", artwork.getArtist().getId());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> paginatedResponse = new HashMap<>();
        paginatedResponse.put("artworks", response);
        paginatedResponse.put("total", artworkPage.getTotalElements());
        paginatedResponse.put("page", artworkPage.getNumber());
        paginatedResponse.put("pageSize", artworkPage.getSize());

        return Response.ok(paginatedResponse).build();
    }
    @GET
    @Path("/count")
    public Response getArtworksCount() {
        long count = artworkService.getArtworksCount();
        return Response.ok().entity(count).build();
    }

}