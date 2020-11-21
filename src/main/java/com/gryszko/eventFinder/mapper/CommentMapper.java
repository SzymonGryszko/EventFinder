package com.gryszko.eventFinder.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.gryszko.eventFinder.dto.CommentDto;
import com.gryszko.eventFinder.model.Comment;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.model.User;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CommentMapper {
    public Comment mapToComment(CommentDto commentDto, Event event, User currentUser) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .creationDate(Instant.now())
                .user(currentUser)
                .build();
    }

    public CommentDto mapCommentEntityToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getEventId())
                .createdAt(TimeAgo.using(comment.getCreationDate().toEpochMilli()))
                .text(comment.getText())
                .username(comment.getUser().getUsername())
                .build();

    }
}
