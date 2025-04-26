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
public class EventRegistrationService {
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public EventRegistration registerForEvent(Long eventId, Long userId) {
        if (eventRegistrationRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new RuntimeException("Already Registered");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setPaymentStatus("UNPAID");
        registration.setTicketCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return eventRegistrationRepository.save(registration);
    }

    public Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId) {
        return eventRegistrationRepository.findByEventIdAndUserId(eventId, userId);
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