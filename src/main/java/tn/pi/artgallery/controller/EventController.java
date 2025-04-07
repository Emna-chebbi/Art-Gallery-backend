// EventController.java
package tn.pi.artgallery.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.pi.artgallery.entities.Event;
import tn.pi.artgallery.entities.EventRegistration;
import tn.pi.artgallery.services.EventService;

import java.util.List;

@Component
@Path("/events")
public class EventController {
    @Autowired
    private EventService eventService;

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
    public Response registerForEvent(@PathParam("eventId") Long eventId, @PathParam("userId") Long userId) {
        EventRegistration registration = eventService.registerForEvent(eventId, userId);
        return Response.ok(registration).build();
    }

    @DELETE
    @Path("/registrations/{id}")
    public Response cancelRegistration(@PathParam("id") Long id) {
        eventService.cancelRegistration(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRegistrations(@PathParam("userId") Long userId) {
        List<EventRegistration> registrations = eventService.getUserRegistrations(userId);
        return Response.ok(registrations).build();
    }

    @GET
    @Path("/{eventId}/is-registered/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isUserRegistered(@PathParam("eventId") Long eventId, @PathParam("userId") Long userId) {
        boolean isRegistered = eventService.isUserRegistered(eventId, userId);
        return Response.ok("{\"registered\":" + isRegistered + "}").build();
    }
}