package com.gryszko.eventFinder.dto;

import java.sql.Date;

public class EventResponse {
    private Long eventId;
    private String title;
    private Date startingDate;
    private Date endDate;
    private String description;
    private String city;
    private String address;
    private String organizerName;
    private Integer numberOfAttendees;
    private String duration;
}
