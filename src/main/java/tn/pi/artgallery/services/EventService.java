// EventService.java
package tn.pi.artgallery.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.pi.artgallery.entities.Event;
import tn.pi.artgallery.entities.EventRegistration;
import tn.pi.artgallery.entities.User;
import tn.pi.artgallery.repository.EventRegistrationRepository;
import tn.pi.artgallery.repository.EventRepository;
import tn.pi.artgallery.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByStartDateAfter(LocalDateTime.now());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setStartDate(eventDetails.getStartDate());
        event.setEndDate(eventDetails.getEndDate());
        event.setLocation(eventDetails.getLocation());
        event.setOnlineUrl(eventDetails.getOnlineUrl());
        event.setPrice(eventDetails.getPrice());
        event.setCapacity(eventDetails.getCapacity());
        event.setImageUrl(eventDetails.getImageUrl());
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public EventRegistration registerForEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setPaymentStatus("pending");
        registration.setTicketCode(UUID.randomUUID().toString());
        registration.setRegistrationDate(LocalDateTime.now());

        return eventRegistrationRepository.save(registration);
    }

    public void cancelRegistration(Long registrationId) {
        eventRegistrationRepository.deleteById(registrationId);
    }

    public List<EventRegistration> getUserRegistrations(Long userId) {
        return eventRegistrationRepository.findByUserId(userId);
    }

    public boolean isUserRegistered(Long eventId, Long userId) {
        return eventRegistrationRepository.existsByEventIdAndUserId(eventId, userId);
    }
}