package com.gryszko.eventFinder.configuration;

import com.gryszko.eventFinder.security.JwtAuthenticationFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    @NotNull
    private String urlFrontend;
    public String getUrlFrontend() {
        return urlFrontend;
    }

    public void setUrlFrontend(String urlFrontend) {
        this.urlFrontend = urlFrontend;
    }

    @NotNull
    private String urlBackend;
    public String getUrlBackend() {
        return urlBackend;
    }

    public void setUrlBackend(String urlBackend) {
        this.urlBackend = urlBackend;
    }

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