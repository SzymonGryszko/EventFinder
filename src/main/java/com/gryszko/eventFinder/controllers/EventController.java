package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.dto.EventResponse;
import com.gryszko.eventFinder.exception.EmailException;
import com.gryszko.eventFinder.exception.EntityAlreadyExistsException;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.exception.UnauthorizedException;
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
    public void createEvent(@RequestBody EventRequest eventRequest) throws NotFoundException {
        eventService.save(eventRequest);
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public EventResponse getEvent(@PathVariable Long id) throws NotFoundException {
        return eventService.getEvent(id);
    }

    @GetMapping("/by-organizer/{username}")
    public List<EventResponse> getEventsByOrganizer(@PathVariable String username) throws NotFoundException, UnauthorizedException {
        System.out.println(username);
        return eventService.getEventsByOrganizer(username);
    }

    @GetMapping("/my-events/{username}")
    public List<EventResponse> getEventsByAttendee(@PathVariable String username) throws NotFoundException {
        return eventService.getEventsByAttendee(username);
    }

    @PostMapping("/eventSignup")
    public void signupUserForEvent(@RequestParam String username, Long eventId) throws NotFoundException, EmailException, EntityAlreadyExistsException {
        eventService.signupUserForEvent(username, eventId);
    }

//    @PutMapping("/update/{id}")
//    public EventResponse updateEvent(@PathVariable Long id) {
//        return eventService.updateEvent(id);
//    }
//
//    @DeleteMapping("/{id}")
//    public EventResponse deleteEvent(@PathVariable Long id) {
//        return eventService.deleteEvent(id);
//    }
//
//    @PostMapping("/{id}/addAttendee")
//    public Integer addAttendeeToEvent(@PathVariable Long id) {
//        return eventService.addAttendeeToEvent(id);
//    }
}
