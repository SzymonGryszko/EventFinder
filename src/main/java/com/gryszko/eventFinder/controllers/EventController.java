package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.dto.EventResponse;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.repository.EventRepository;
import com.gryszko.eventFinder.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {

    private EventService eventService;

    @PostMapping
    public void createEvent(@RequestBody EventRequest eventRequest) {
        eventRequest.save(eventRequest);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventResponse getEvent(@PathVariable Long id) {
        return  eventService.getEvent(id);
    }

    @GetMapping("/by-organizer/{username}")
    public List<EventResponse> getEventsByOrganizer(String username) {
        return eventService.getEventsByOrganizer(username);
    }

    @GetMapping("/my-events/{username}")
    public List<EventResponse> getEventsByAttendee(String username) {
        return eventService.getEventsByAttendee(username);
    }

    @PutMapping("/update/{id}")
    public EventResponse updateEvent(@PathVariable Long id){
        return eventService.updateEvent(id);
    }

    @DeleteMapping("/{id}")
    public EventResponse deleteEvent(@PathVariable Long id) {
        return  eventService.deleteEvent(id);
    }

    @PostMapping("/{id}/addAttendee")
    public Integer addAttendeeToEvent(@PathVariable Long id) {
        return eventService.addAttendeeToEvent(id);
    }
}
