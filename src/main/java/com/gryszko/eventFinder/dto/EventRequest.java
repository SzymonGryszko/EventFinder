package com.gryszko.eventFinder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@NotEmpty
public class EventRequest {
    private String title;
    private String startingDate;
    private String endDate;
    private String description;
    private String city;
    private String address;

}
