package com.gryszko.eventFinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventSignuporResignRequest {
    private String username;
    private Long eventId;
}
