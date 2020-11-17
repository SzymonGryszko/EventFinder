package com.gryszko.eventFinder.security;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.google.common.net.HttpHeaders;

@NoArgsConstructor
@Data
@Component
@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationTime;

    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }

}
