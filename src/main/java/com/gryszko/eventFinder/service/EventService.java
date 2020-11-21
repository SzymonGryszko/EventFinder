package com.gryszko.eventFinder.service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.dto.EventResponse;
import com.gryszko.eventFinder.exception.*;
import com.gryszko.eventFinder.mapper.EventMapper;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.model.NotificationEmail;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.repository.EventRepository;
import com.gryszko.eventFinder.repository.UserRepository;
import com.gryszko.eventFinder.utils.EventDateFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventDateFormatter eventDateFormatter;
    private final AuthService authService;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final MailService mailService;

    public void save(EventRequest eventRequest) throws UnauthorizedException {
        Event eventToBeSaved = eventMapper.mapEventRequestToEntity(eventRequest);
        eventRepository.save(eventToBeSaved);

    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::mapEventEntityToEventResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEvent(Long id) throws NotFoundException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        return eventMapper.mapEventEntityToEventResponse(event);

    }

    public List<EventResponse> getEventsByOrganizer(String username) throws NotFoundException, UnauthorizedException {

        User user = authService.getCurrentUser();

        if (user.getUsername().equals(username)) {
        System.out.println(user.getUsername());
            return eventRepository
                    .getAllByOrganizer(user)
                    .stream()
                    .map(eventMapper::mapEventEntityToEventResponse)
                    .collect(Collectors.toList());
        } else {
            throw new UnauthorizedException("You cannot access other organizers events");

        }
    }

    public List<EventResponse> getEventsByAttendee(String username) throws NotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        return eventRepository
                .getAllByAttendeesContains(user)
                .stream()
                .map(eventMapper::mapEventEntityToEventResponse)
                .collect(Collectors.toList());
    }

    public void signupUserForEvent(String username, Long eventId) throws NotFoundException, EmailException, EntityAlreadyExistsException, ExpiryException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));
        User organizer = event.getOrganizer();

        if (event.getAttendees().contains(user)) {
            throw new EntityAlreadyExistsException("You have already signed up for this event");
        }else if(event.getEndDate().after(Date.from(Instant.now()))) {
            throw new ExpiryException("You cannot signup for event that has already ended");
        } else {
            event.getAttendees().add(user);
            eventRepository.save(event);
        }

        mailService.sendMail(new NotificationEmail(username + " signed up for your event - " + event.getTitle(),
                organizer.getEmail(), "New person just signed up for your event, check it out http://localhost:8080/api/events/" + eventId));

    }

    public EventResponse updateEvent(Long id, EventRequest eventRequest) throws NotFoundException, EmailException, UnauthorizedException {
        Event eventEntity = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        Set<User> attendees = eventEntity.getAttendees();
        Event event = eventMapper.mapEventRequestToEntity(eventRequest);
        event.setEventId(id);
        event.setAttendees(attendees);
        Event updatedEvent = eventRepository.save(event);

        sendNotificationEmailToAllAttendees(attendees, id, eventEntity.getTitle());
        return eventMapper.mapEventEntityToEventResponse(updatedEvent);
    }

    private void sendNotificationEmailToAllAttendees(Set<User> attendees, Long eventId, String title) throws EmailException {
        for (User attendee:attendees) {
            mailService.sendMail(new NotificationEmail("One of your events has just been updated!",
                    attendee.getEmail(), "Even you signed up for - " + title +  "has just been updated, check it out http://localhost:8080/api/events/" + eventId));
        }
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
