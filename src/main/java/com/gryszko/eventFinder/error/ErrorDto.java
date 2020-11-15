package com.gryszko.eventFinder.error;

import java.time.LocalDateTime;

public class ErrorDto {

    private String reason;
    private LocalDateTime time = LocalDateTime.now();

    public ErrorDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getTime() {
        return time;
    }
}

