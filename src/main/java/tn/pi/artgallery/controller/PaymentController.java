package tn.pi.artgallery.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.pi.artgallery.entities.Artwork;
import tn.pi.artgallery.entities.Payment;
import tn.pi.artgallery.services.ArtworkService;
import tn.pi.artgallery.services.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentController {

    private final ArtworkService artworkService;
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(ArtworkService artworkService, PaymentService paymentService) {
        this.artworkService = artworkService;
        this.paymentService = paymentService;
    }

    @POST
    @Path("/process/{artworkId}")
    public Response processPayment(
            @PathParam("artworkId") Long artworkId,
            Map<String, Object> paymentRequest) {

        try {
            Double amount = Double.valueOf(paymentRequest.get("amount").toString());

            Optional<Artwork> optionalArtwork = artworkService.getArtworkById(artworkId);
            if (optionalArtwork.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Artwork not found"))
                        .build();
            }

            Artwork artwork = optionalArtwork.get();
            Payment payment = new Payment(artwork, amount, "Completed");
            payment = paymentService.savePayment(payment);

            // Mark artwork as sold
            artwork.setAvailable(false);
            artworkService.saveArtwork(artwork);

            return Response.ok()
                    .entity(Map.of(
                            "message", "Payment processed successfully",
                            "payment", Map.of(
                                    "id", payment.getId(),
                                    "artwork", Map.of(
                                            "id", artwork.getId(),
                                            "title", artwork.getTitle()
                                    ),
                                    "amount", payment.getAmount(),
                                    "status", payment.getStatus()
                            )
                    ))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}