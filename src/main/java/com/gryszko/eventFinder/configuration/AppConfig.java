package com.gryszko.eventFinder.configuration;

import com.gryszko.eventFinder.mapper.EventMapper;
import com.gryszko.eventFinder.security.JwtAuthenticationFilter;
import com.gryszko.eventFinder.utils.EventDateFormatter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public StringBuilder stringBuilder() {
        return new StringBuilder();
    }

    @Bean
    public FilterRegistrationBean registration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }
}