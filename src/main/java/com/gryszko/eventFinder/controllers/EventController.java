package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.EventRequest;
import com.gryszko.eventFinder.dto.EventResponse;
import com.gryszko.eventFinder.exception.*;
import com.gryszko.eventFinder.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {

    private EventService eventService;

    @PostMapping
    public void createEvent(@Valid @RequestBody EventRequest eventRequest) throws UnauthorizedException, BadRequestException {
        eventService.save(eventRequest);
    }

    @GetMapping
    public List<EventResponse> getAllEventsWithStartDateTodayOrLater(@RequestParam(required = false) String city, String keyWord) {
        return eventService.getAllEventsWithStartDateTodayOrLater(city, keyWord);
    }

    @GetMapping("/cities")
    public Set<String> getAllCitiesFromEvents() {
        return eventService.getAllCitiesFromEvents();
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
    public void signupUserForEvent(@RequestParam String username, Long eventId) throws NotFoundException, EmailException, ConflictException, BadRequestException {
        eventService.signupUserForEvent(username, eventId);
    }

    @PutMapping("/update/{id}")
    public EventResponse updateEvent(@PathVariable Long id, @RequestBody EventRequest eventRequest) throws NotFoundException, EmailException, UnauthorizedException {
        return eventService.updateEvent(id, eventRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) throws NotFoundException, EmailException {
        eventService.deleteEvent(id);
    }

}
