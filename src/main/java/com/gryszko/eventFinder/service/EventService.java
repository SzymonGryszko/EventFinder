package com.gryszko.eventFinder.service;

import com.google.common.base.Strings;
import com.gryszko.eventFinder.configuration.AppConfig;
import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.dto.EventResponse;
import com.gryszko.eventFinder.dto.EventSignuporResignRequest;
import com.gryszko.eventFinder.exception.*;
import com.gryszko.eventFinder.mapper.EventMapper;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.model.NotificationEmail;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.repository.EventRepository;
import com.gryszko.eventFinder.repository.UserRepository;
import com.gryszko.eventFinder.utils.EventDateFormatter;
import com.gryszko.eventFinder.utils.EventFinderStringBuilder;
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
    private final AuthService authService;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final EventFinderStringBuilder stringBuilder;
    private final EventDateFormatter eventDateFormatter;
    private final AppConfig appConfig;

    public void save(EventRequest eventRequest) throws UnauthorizedException, BadRequestException {
        Date startDate = eventDateFormatter.formatStringDateToSQLDate(eventRequest.getStartingDate());
        Date endDate = eventDateFormatter.formatStringDateToSQLDate(eventRequest.getEndDate());
        if (startDate.compareTo(endDate) == 1) {
            throw new BadRequestException("End date before start date");
        }
        Event eventToBeSaved = eventMapper.mapEventRequestToEntity(eventRequest);
        eventRepository.save(eventToBeSaved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEventsWithStartDateTodayOrLater(String city, String keyWord) {

        java.util.Date today = Date.from(Instant.now());

        if (!Strings.isNullOrEmpty(city) && Strings.isNullOrEmpty(keyWord)) {
            return eventRepository.findAllByCityContainingAndStartingDateGreaterThanEqualOrderByStartingDateAsc(city, today)
                    .stream()
                    .map(eventMapper::mapEventEntityToEventResponse)
                    .collect(Collectors.toList());
        } else if (Strings.isNullOrEmpty(city) && !Strings.isNullOrEmpty(keyWord)) {
            return eventRepository.searchByKeyword(keyWord, today)
                    .stream()
                    .map(eventMapper::mapEventEntityToEventResponse)
                    .collect(Collectors.toList());
        } else if (!Strings.isNullOrEmpty(city) && !Strings.isNullOrEmpty(keyWord)) {
            return eventRepository.searchByCityAndKeyword(city, keyWord, today)
                    .stream()
                    .map(eventMapper::mapEventEntityToEventResponse)
                    .collect(Collectors.toList());
        }
        return eventRepository.findAllByStartingDateGreaterThanEqualOrderByStartingDateAsc(today)
                .stream()
                .map(eventMapper::mapEventEntityToEventResponse)
                .collect(Collectors.toList());

    }

    public EventResponse getEvent(Long id) throws NotFoundException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        return eventMapper.mapEventEntityToEventResponse(event);

    }

    public List<EventResponse> getEventsByOrganizer(String username) throws UnauthorizedException, NotFoundException {

        String currentUser = authService.getCurrentUser().getUsername();

        if (currentUser.equals(username)) {
            User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
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

    public void signupUserForEvent(EventSignuporResignRequest eventSignuporResignRequest) throws NotFoundException, EmailException, ConflictException, BadRequestException {
        User user = userRepository.findByUsername(eventSignuporResignRequest.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventSignuporResignRequest.getEventId()).orElseThrow(() -> new NotFoundException("Event not found"));
        User organizer = event.getOrganizer();
        java.util.Date today = Date.from(Instant.now());


        if (event.getAttendees().contains(user)) {
            throw new ConflictException("You have already signed up for this event");
        } else if (event.getEndDate().compareTo(today) > 1) {
            throw new BadRequestException("You cannot signup for event that has already ended");
        } else {
            event.getAttendees().add(user);
            eventRepository.save(event);
        }

        mailService.sendMail(new NotificationEmail(eventSignuporResignRequest.getUsername() + " signed up for your event - " + event.getTitle(),
                organizer.getEmail(), "New person just signed up for your event, check it out" + appConfig.getUrlFrontend() + "/events/" + eventSignuporResignRequest.getEventId()));

    }

    public EventResponse updateEvent(Long id, EventRequest eventRequest) throws NotFoundException, EmailException, UnauthorizedException {
        Event eventEntity = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        Set<User> attendees = eventEntity.getAttendees();
        Event event = eventMapper.mapEventRequestToEntity(eventRequest);
        event.setEventId(id);
        event.setAttendees(attendees);
        Event updatedEvent = eventRepository.save(event);

        String emailTitle = "One of your events has just been updated!";
        String emailBody = stringBuilder.build("Even you signed up for - ", event.getTitle(), " has just been updated, check it out " + appConfig.getUrlFrontend() + "/events/", event.getEventId().toString());

        sendNotificationEmailToAllAttendees(attendees, emailTitle, emailBody);
        return eventMapper.mapEventEntityToEventResponse(updatedEvent);
    }

    private void sendNotificationEmailToAllAttendees(Set<User> attendees, String emailTitle, String emailBody) throws EmailException {
        for (User attendee : attendees) {
            mailService.sendMail(new NotificationEmail(emailTitle, attendee.getEmail(), emailBody));
        }
    }

    public void deleteEvent(Long id) throws NotFoundException, EmailException {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
        Set<User> attendees = event.getAttendees();
        String emailTitle = "One of your events has just been cancelled!";
        String emailBody = stringBuilder.build("Even you signed up for - ", event.getTitle(), " has just been cancelled, contact ", event.getOrganizer().getEmail(), " for more info");

        sendNotificationEmailToAllAttendees(attendees, emailTitle, emailBody);
        eventRepository.deleteById(id);
    }

    public Set<String> getAllCitiesFromEvents() {
        return eventRepository.findAllByStartingDateGreaterThanEqualOrderByStartingDateAsc(java.util.Date.from(Instant.now()))
                .stream()
                .map(event -> event.getCity())
                .collect(Collectors.toSet());
    }

    public void resignFromEvent(EventSignuporResignRequest eventSignuporResignRequest) throws NotFoundException, EmailException {
        User user = userRepository.findByUsername(eventSignuporResignRequest.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventSignuporResignRequest.getEventId()).orElseThrow(() -> new NotFoundException("Event not found"));
        User organizer = event.getOrganizer();

        if (!event.getAttendees().contains(user)) {
            throw new NotFoundException("User was not signed up for this event");
        } else {
            event.getAttendees().remove(user);
            eventRepository.save(event);
        }
        mailService.sendMail(new NotificationEmail(eventSignuporResignRequest.getUsername() + " resigned from your - " + event.getTitle(),
                organizer.getEmail(), "One person just resigned from your event, check it out " + appConfig.getUrlFrontend() + "/events/" + eventSignuporResignRequest.getEventId()));

    }
}
