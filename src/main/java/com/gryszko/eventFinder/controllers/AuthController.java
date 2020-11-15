package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.RegisterRequest;
import com.gryszko.eventFinder.exception.EmailException;
import com.gryszko.eventFinder.exception.EntityAlreadyExistsException;
import com.gryszko.eventFinder.exception.PasswordValidationException;
import com.gryszko.eventFinder.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody RegisterRequest registerRequest) throws EmailException, PasswordValidationException, EntityAlreadyExistsException {
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration successful", HttpStatus.OK);
    }

}
