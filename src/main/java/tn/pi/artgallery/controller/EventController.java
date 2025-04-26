package tn.pi.artgallery.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.pi.artgallery.entities.Event;
import tn.pi.artgallery.entities.EventRegistration;
import tn.pi.artgallery.services.EventService;
import tn.pi.artgallery.services.EventRegistrationService;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Path("/events")
public class EventController {
    private final EventService eventService;
    private final EventRegistrationService eventRegistrationService;

    @Autowired
    public EventController(EventService eventService,
                           EventRegistrationService eventRegistrationService) {
        this.eventService = eventService;
        this.eventRegistrationService = eventRegistrationService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return Response.ok(events).build();
    }

    @GET
    @Path("/upcoming")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return Response.ok(events).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventById(@PathParam("id") Long id) {
        return eventService.getEventById(id)
                .map(event -> Response.ok(event).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEvent(Event event) {
        Event createdEvent = eventService.createEvent(event);
        return Response.status(Response.Status.CREATED).entity(createdEvent).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEvent(@PathParam("id") Long id, Event event) {
        Event updatedEvent = eventService.updateEvent(id, event);
        return Response.ok(updatedEvent).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@PathParam("id") Long id) {
        eventService.deleteEvent(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{eventId}/register/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerForEvent(@PathParam("eventId") Long eventId,
                                     @PathParam("userId") Long userId) {
        try {
            EventRegistration registration = eventRegistrationService.registerForEvent(eventId, userId);
            return Response.ok(registration).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{eventId}/registration/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRegistration(@PathParam("eventId") Long eventId,
                                    @PathParam("userId") Long userId) {
        Optional<EventRegistration> registration = eventRegistrationService.findByEventIdAndUserId(eventId, userId);
        return registration.map(reg -> Response.ok(reg).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }


    @DELETE
    @Path("/registrations/{id}")
    public Response cancelRegistration(@PathParam("id") Long id) {
        eventRegistrationService.cancelRegistration(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRegistrations(@PathParam("userId") Long userId) {
        List<EventRegistration> registrations = eventRegistrationService.getUserRegistrations(userId);
        return Response.ok(registrations).build();
    }

    @GET
    @Path("/{eventId}/is-registered/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isUserRegistered(@PathParam("eventId") Long eventId,
                                     @PathParam("userId") Long userId) {
        boolean isRegistered = eventRegistrationService.isUserRegistered(eventId, userId);
        return Response.ok(new HashMap<String, Boolean>() {{
            put("registered", isRegistered);
        }}).build();
    }
}