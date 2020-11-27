package com.gryszko.eventFinder.repository;

import com.gryszko.eventFinder.dto.CommentDto;
import com.gryszko.eventFinder.model.Comment;
import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventOrderByIdDesc(Event event);
    List<Comment> findAllByUser(User user);
}
