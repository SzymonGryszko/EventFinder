package com.gryszko.eventFinder.repository;

import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByStartingDateGreaterThanEqualOrderByStartingDateDesc(Date today);
    List<Event> findAllByCityContainingAndStartingDateGreaterThanEqualOrderByStartingDateDesc(String city, Date today);
    List<Event> findAllByDescriptionContainingOrTitleContainingAndStartingDateGreaterThanEqualOrderByStartingDateDesc(String keyDesc, String keyTitle, Date today);
    List<Event> getAllByOrganizer(User user);
    List<Event> getAllByAttendeesContains(User user);

}
