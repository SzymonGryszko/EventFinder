package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.CommentDto;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public void createComment(@RequestBody CommentDto commentDto) throws NotFoundException {
        commentService.save(commentDto);
    }

    @GetMapping("/by-event/{eventId}")
    public List<CommentDto> getAllCommentsForPost(@PathVariable Long eventId) throws NotFoundException {
        return commentService.getAllCommentsForEvent(eventId);
    }

    @GetMapping("/by-user/{username}")
    public List<CommentDto> getAllCommentsForUser(@PathVariable String username) throws NotFoundException {
        return commentService.getAllCommentsForUser(username);
    }

}
