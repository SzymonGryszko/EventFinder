package com.gryszko.eventFinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventResponse {
    private Long eventId;
    private String title;
    private String startingDate;
    private String endDate;
    private String description;
    private String city;
    private String address;
    private String organizerName;
    private Integer numberOfAttendees;
    private String lastUpdated;
}
