package com.gryszko.eventFinder.service;

import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.repository.EventRepository;
import com.gryszko.eventFinder.utils.EventDateFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventDateFormatter eventDateFormatter;
    private final AuthService authService;

    public void save(EventRequest eventRequest) throws NotFoundException {
        Event eventToBeSaved = mapEventRequestToEntity(eventRequest);
        eventRepository.save(eventToBeSaved);

    }

    private Event mapEventRequestToEntity(EventRequest eventRequest) throws NotFoundException {
        return Event.builder()
                .title(eventRequest.getTitle())
                .startingDate(eventDateFormatter.formatStringDateToSQLDate(eventRequest.getStartingDate()))
                .endDate(eventDateFormatter.formatStringDateToSQLDate(eventRequest.getStartingDate()))
                .description(eventRequest.getDescription())
                .city(eventRequest.getCity())
                .address(eventRequest.getAddress())
                .organizer(authService.getCurrentUser())
                .attendees(new HashSet<>())
                .build();
    }
}
