package com.gryszko.eventFinder.service;

import com.gryszko.eventFinder.dto.CommentDto;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.exception.UnauthorizedException;
import com.gryszko.eventFinder.mapper.CommentMapper;
import com.gryszko.eventFinder.model.Comment;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.repository.CommentRepository;
import com.gryszko.eventFinder.repository.EventRepository;
import com.gryszko.eventFinder.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CommentService {

    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public void save(CommentDto commentDto) throws NotFoundException, UnauthorizedException {
        Event event = eventRepository.findById(commentDto.getEventId()).orElseThrow(() -> new NotFoundException("Event not found " + commentDto.getEventId().toString()));
        Comment comment = commentMapper.mapToComment(commentDto, event, authService.getCurrentUser());
        commentRepository.save(comment);
    }


    public List<CommentDto> getAllCommentsForEvent(Long eventId) throws NotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found " + eventId.toString()));
        return commentRepository.findByEventOrderByIdDesc(event)
                .stream()
                .map(commentMapper::mapCommentEntityToDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getAllCommentsForUser(String username) throws NotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapCommentEntityToDto)
                .collect(Collectors.toList());
    }
}
