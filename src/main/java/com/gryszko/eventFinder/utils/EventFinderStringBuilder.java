package com.gryszko.eventFinder.utils;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventFinderStringBuilder {

    private final StringBuilder builder;

    public String build(String... strings) {
        for (String arg : strings) {
            builder.append(arg);
        }
        return builder.toString();
    }

}
