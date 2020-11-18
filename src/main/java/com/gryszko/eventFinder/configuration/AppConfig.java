package com.gryszko.eventFinder.configuration;

import com.gryszko.eventFinder.utils.EventDateFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public EventDateFormatter eventDateFormatter() {
        return new EventDateFormatter();
    }
}