package tn.pi.artgallery.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tn.pi.artgallery.entities.EventPayment;
import tn.pi.artgallery.entities.EventRegistration;
import tn.pi.artgallery.services.EventPaymentService;
import tn.pi.artgallery.services.EventRegistrationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Path("/payments/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventPaymentController {

    private final EventPaymentService paymentService;
    private final EventRegistrationService registrationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public EventPaymentController(EventPaymentService paymentService,
                                  EventRegistrationService registrationService,
                                  SimpMessagingTemplate messagingTemplate) {
        this.paymentService = paymentService;
        this.registrationService = registrationService;
        this.messagingTemplate = messagingTemplate;
    }

    @POST
    @Path("/process")
    public Response processEventPayment(Map<String, Object> paymentRequest) {
        try {
            Long eventId = Long.valueOf(paymentRequest.get("eventId").toString());
            Long userId = Long.valueOf(paymentRequest.get("userId").toString());
            String paymentMethod = paymentRequest.get("paymentMethod").toString();
            String cardLastFour = paymentRequest.get("cardLastFour").toString();

            // Find existing registration or create new one
            EventRegistration registration = registrationService
                    .findByEventIdAndUserId(eventId, userId)
                    .orElseGet(() -> registrationService.registerForEvent(eventId, userId));

            // Process payment
            String transactionId = "EVT-" + System.currentTimeMillis();
            EventPayment payment = paymentService.processPayment(
                    registration.getId(),
                    paymentMethod,
                    cardLastFour,
                    transactionId
            );

            return Response.ok(Map.of(
                    "success", true,
                    "payment", paymentToMap(payment),
                    "ticketCode", registration.getTicketCode()
            )).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserEventPayments(@PathParam("userId") Long userId) {
        try {
            List<EventPayment> payments = paymentService.getUserEventPayments(userId);
            return Response.ok()
                    .entity(payments.stream()
                            .map(this::paymentToMap)
                            .collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}/history")
    public Response getUserEventPaymentHistory(@PathParam("userId") Long userId) {
        try {
            List<EventPayment> payments = paymentService.getUserEventPaymentHistory(userId);
            return Response.ok()
                    .entity(payments.stream()
                            .map(this::paymentToDetailedMap)
                            .collect(Collectors.toList()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/admin")
    public Response getAllEventPayments(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EventPayment> paymentPage = paymentService.getAllEventPayments(pageable);

        return Response.ok()
                .entity(createPaginatedResponse(paymentPage))
                .build();
    }

    @GET
    @Path("/admin/search")
    public Response searchEventPayments(
            @QueryParam("query") String query,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EventPayment> paymentPage = paymentService.searchEventPayments(query, pageable);

        return Response.ok()
                .entity(createPaginatedResponse(paymentPage))
                .build();
    }

    @GET
    @Path("/count")
    public Response getEventTransactionsCount() {
        long count = paymentService.getEventTransactionsCount();
        return Response.ok().entity(count).build();
    }

    private void sendPaymentNotification(EventPayment payment) {
        Map<String, Object> notification = Map.of(
                "paymentId", payment.getId(),
                "message", "Event payment processed successfully",
                "amount", payment.getAmount(),
                "date", payment.getPaymentDate().toString(),
                "eventTitle", payment.getRegistration().getEvent().getTitle(),
                "cardLastFour", payment.getCardLastFour()
        );

        messagingTemplate.convertAndSendToUser(
                payment.getRegistration().getUser().getId().toString(),
                "/queue/event-payments",
                notification
        );
    }

    private Map<String, Object> paymentToMap(EventPayment payment) {
        return Map.of(
                "id", payment.getId(),
                "event", Map.of(
                        "id", payment.getRegistration().getEvent().getId(),
                        "title", payment.getRegistration().getEvent().getTitle()
                ),
                "amount", payment.getAmount(),
                "status", payment.getStatus(),
                "date", payment.getPaymentDate()
        );
    }

    private Map<String, Object> paymentToDetailedMap(EventPayment payment) {
        return Map.of(
                "id", payment.getId(),
                "event", Map.of(
                        "id", payment.getRegistration().getEvent().getId(),
                        "title", payment.getRegistration().getEvent().getTitle(),
                        "date", payment.getRegistration().getEvent().getStartDate()
                ),
                "user", Map.of(
                        "id", payment.getRegistration().getUser().getId(),
                        "name", payment.getRegistration().getUser().getFullName()
                ),
                "amount", payment.getAmount(),
                "status", payment.getStatus(),
                "date", payment.getPaymentDate(),
                "paymentMethod", payment.getPaymentMethod(),
                "transactionId", payment.getTransactionId(),
                "cardLastFour", payment.getCardLastFour()
        );
    }

    private Map<String, Object> createPaginatedResponse(Page<EventPayment> paymentPage) {
        return Map.of(
                "success", true,
                "payments", paymentPage.getContent().stream()
                        .map(this::paymentToDetailedMap)
                        .collect(Collectors.toList()),
                "total", paymentPage.getTotalElements(),
                "page", paymentPage.getNumber(),
                "size", paymentPage.getSize()
        );
    }
}