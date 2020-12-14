package com.gryszko.eventFinder.repository;

import com.gryszko.eventFinder.model.Event;
import com.gryszko.eventFinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByStartingDateGreaterThanEqualOrderByStartingDateAsc(Date today);
    List<Event> findAllByCityContainingAndStartingDateGreaterThanEqualOrderByStartingDateAsc(String city, Date today);
    @Query(value = "select e from Event e where (e.description like %?1 or e.title like %?1) and e.startingDate >= ?2 order by e.startingDate asc")
    List<Event> searchByKeyword(String key, Date today);
    @Query(value = "select e from Event e where e.city = ?1 and (e.description like %?2 or e.title like %?2) and e.startingDate >= ?3 order by e.startingDate asc")
    List<Event> searchByCityAndKeyword(String city, String key, Date today);
    List<Event> getAllByOrganizer(User user);
    List<Event> getAllByAttendeesContains(User user);

}
