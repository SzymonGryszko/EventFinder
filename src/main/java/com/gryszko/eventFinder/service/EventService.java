package com.gryszko.eventFinder.service;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.dto.EventResponse;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.repository.EventRepository;
import com.gryszko.eventFinder.utils.EventDateFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventDateFormatter eventDateFormatter;
    private final AuthService authService;

    public void save(EventRequest eventRequest) throws NotFoundException {
        Event eventToBeSaved = mapEventRequestToEntity(eventRequest);
        eventRepository.save(eventToBeSaved);

    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapEventEntityToEventResponse)
                .collect(Collectors.toList());
    }

    public Event mapEventRequestToEntity(EventRequest eventRequest) throws NotFoundException {
        return Event.builder()
                .title(eventRequest.getTitle())
                .startingDate(eventDateFormatter.formatStringDateToSQLDate(eventRequest.getStartingDate()))
                .endDate(eventDateFormatter.formatStringDateToSQLDate(eventRequest.getStartingDate()))
                .description(eventRequest.getDescription())
                .city(eventRequest.getCity())
                .address(eventRequest.getAddress())
                .createdAt(Instant.now())
                .organizer(authService.getCurrentUser())
                .attendees(new HashSet<>())
                .build();
    }

    @Transactional(readOnly = true)
    public EventResponse mapEventEntityToEventResponse(Event event) {
        return EventResponse.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .startingDate(event.getStartingDate())
                .endDate(event.getEndDate())
                .description(event.getDescription())
                .city(event.getCity())
                .organizerName(event.getOrganizer().getUsername())
                .numberOfAttendees(event.getAttendees().size())
                .timeAgo(TimeAgo.using(event.getCreatedAt().toEpochMilli()))
                .build();
    }


}
