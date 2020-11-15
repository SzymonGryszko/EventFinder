package com.gryszko.eventFinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationResponse {

    private String authenticationToken;
    private String username;

}
