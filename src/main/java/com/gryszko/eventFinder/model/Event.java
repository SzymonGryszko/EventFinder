package com.gryszko.eventFinder.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.sql.Date;
import java.util.Set;

import static javax.persistence.GenerationType.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long eventId;
    @NotBlank(message = "Event title cannot be empty or null")
    private String title;
    private Date startingDate;
    private Date endDate;
    @NotBlank(message = "Description cannot be empty")
    @Lob
    private String description;
    @OneToOne
    private User organizer;
    @ManyToMany
    private Set<User> attendees;
}
