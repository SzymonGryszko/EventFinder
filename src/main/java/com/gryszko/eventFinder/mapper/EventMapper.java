package com.gryszko.eventFinder.mapper;


import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.dto.EventResponse;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.exception.UnauthorizedException;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.service.AuthService;
import com.gryszko.eventFinder.utils.EventDateFormatter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;

@AllArgsConstructor
@Component
public class EventMapper {

    private final EventDateFormatter eventDateFormatter;
    private final AuthService authService;


    public Event mapEventRequestToEntity(EventRequest eventRequest) throws UnauthorizedException {
        return Event.builder()
                .title(eventRequest.getTitle())
                .startingDate(eventDateFormatter.formatStringDateToSQLDate(eventRequest.getStartingDate()))
                .endDate(eventDateFormatter.formatStringDateToSQLDate(eventRequest.getStartingDate()))
                .description(eventRequest.getDescription())
                .city(eventRequest.getCity())
                .address(eventRequest.getAddress())
                .lastUpdated(Instant.now())
                .organizer(authService.getCurrentUser())
                .attendees(new HashSet<>())
                .build();
    }

    public EventResponse mapEventEntityToEventResponse(Event event) {
        return EventResponse.builder()
                .eventId(event.getEventId())
                .title(event.getTitle())
                .startingDate(event.getStartingDate().toString())
                .endDate(event.getEndDate().toString())
                .description(event.getDescription())
                .city(event.getCity())
                .address(event.getAddress())
                .organizerName(event.getOrganizer().getUsername())
                .numberOfAttendees(event.getAttendees().size())
                .lastUpdated(TimeAgo.using(event.getLastUpdated().toEpochMilli()))
                .build();
    }


}
