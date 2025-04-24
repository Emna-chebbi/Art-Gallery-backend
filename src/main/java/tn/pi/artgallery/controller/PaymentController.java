package tn.pi.artgallery.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tn.pi.artgallery.entities.*;
import tn.pi.artgallery.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentController {

    private final ArtworkService artworkService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PaymentController(ArtworkService artworkService,
                             PaymentService paymentService,
                             UserService userService,
                             SimpMessagingTemplate messagingTemplate) {
        this.artworkService = artworkService;
        this.paymentService = paymentService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @POST
    @Path("/process")
    public Response processPayment(Map<String, Object> paymentRequest) {
        try {
            Long artworkId = Long.valueOf(paymentRequest.get("artworkId").toString());
            Long userId = Long.valueOf(paymentRequest.get("userId").toString());
            Double amount = Double.valueOf(paymentRequest.get("amount").toString());

            Optional<Artwork> optionalArtwork = artworkService.getArtworkById(artworkId);
            if (optionalArtwork.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Artwork not found"))
                        .build();
            }

            Optional<User> optionalUser = userService.getUserById(userId);
            if (optionalUser.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "User not found"))
                        .build();
            }

            Artwork artwork = optionalArtwork.get();
            User user = optionalUser.get();

            Payment payment = new Payment(artwork, user, amount, "Completed");
            payment = paymentService.savePayment(payment);

            // Mark artwork as sold
            artwork.setAvailable(false);
            artworkService.saveArtwork(artwork);

            // Envoyer la notification WebSocket
            sendPaymentNotification(userId, payment);

            return Response.ok()
                    .entity(Map.of(
                            "message", "Payment processed successfully",
                            "payment", paymentToMap(payment)
                    ))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    private void sendPaymentNotification(Long userId, Payment payment) {
        Map<String, Object> notification = Map.of(
                "paymentId", payment.getId(),
                "message", "Paiement effectué avec succès",
                "amount", payment.getAmount(),
                "date", payment.getDate().toString(),
                "artworkTitle", payment.getArtwork().getTitle()
        );

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/payments",
                notification
        );
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserPayments(@PathParam("userId") Long userId) {
        try {
            List<Payment> payments = paymentService.findByUserId(userId);
            return Response.ok()
                    .entity(payments.stream()
                            .map(this::paymentToMap)
                            .toList())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    private Map<String, Object> paymentToMap(Payment payment) {
        return Map.of(
                "id", payment.getId(),
                "artwork", Map.of(
                        "id", payment.getArtwork().getId(),
                        "title", payment.getArtwork().getTitle()
//                        "imageUrl", payment.getArtwork().getImageUrl()
                ),
                "amount", payment.getAmount(),
                "status", payment.getStatus(),
                "date", payment.getDate()
        );
    }
    @GET
    @Path("/user/{userId}/history")
    public Response getPaymentHistory(@jakarta.websocket.server.PathParam("userId") Long userId) {
        try {
            List<Payment> payments = paymentService.findByUserIdOrderByDateDesc(userId);
            return Response.ok()
                    .entity(payments.stream()
                            .map(this::paymentToDetailedMap)
                            .toList())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    private Map<String, Object> paymentToDetailedMap(Payment payment) {
        return Map.of(
                "id", payment.getId(),
                "artwork", Map.of(
                        "id", payment.getArtwork().getId(),
                        "title", payment.getArtwork().getTitle(),
                        "imageUrl", payment.getArtwork().getImageUrl(),
                        "artist", payment.getArtwork().getArtist().getName()
                ),
                "amount", payment.getAmount(),
                "status", payment.getStatus(),
                "date", payment.getDate(),
                "paymentMethod", "Carte Bancaire", // Vous pouvez stocker cette info lors du paiement
                "transactionId", "TXN-" + payment.getId().toString().substring(0, 8).toUpperCase()
        );
    }
    // Add these methods to your existing PaymentController
    @GET
    @Path("/admin")
    public Response getAllPayments(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = paymentService.getAllPayments(pageable);

        return Response.ok()
                .entity(createPaginatedPaymentResponse(paymentPage))
                .build();
    }

    @GET
    @Path("/admin/search")
    public Response searchPayments(
            @QueryParam("query") String query,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = paymentService.searchPayments(query, pageable);

        return Response.ok()
                .entity(createPaginatedPaymentResponse(paymentPage))
                .build();
    }

    private Map<String, Object> createPaginatedPaymentResponse(Page<Payment> paymentPage) {
        return Map.of(
                "success", true,
                "payments", paymentPage.getContent().stream()
                        .map(this::convertToAdminPayment)
                        .collect(Collectors.toList()),
                "total", paymentPage.getTotalElements(),
                "page", paymentPage.getNumber(),
                "size", paymentPage.getSize()
        );
    }

    private Map<String, Object> convertToAdminPayment(Payment payment) {
        return Map.of(
                "id", payment.getId(),
                "artwork", Map.of(
                        "id", payment.getArtwork().getId(),
                        "title", payment.getArtwork().getTitle(),
                        "imageUrl", "/artworks/" + payment.getArtwork().getId() + "/image"
                ),
                "user", Map.of(
                        "id", payment.getUser().getId(),
                        "name", payment.getUser().getFullName(),
                        "email", payment.getUser().getEmail()
                ),
                "amount", payment.getAmount(),
                "status", payment.getStatus(),
                "date", payment.getDate(),
                "paymentMethod", "Credit Card" // Can be stored in Payment entity
        );
    }
    @GET
    @Path("/count")
    public Response getTransactionsCount() {
        long count = paymentService.getTransactionsCount();
        return Response.ok().entity(count).build();
    }

//    @Path("/recent")
//    public Response getRecentTransactions() {
//        List<Payment> payments = paymentService.getRecentTransactions();
//        return Response.ok().entity(payments).build();
//    }
}