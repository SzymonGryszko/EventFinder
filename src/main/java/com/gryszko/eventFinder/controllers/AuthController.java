package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.AuthenticationResponse;
import com.gryszko.eventFinder.dto.LoginRequest;
import com.gryszko.eventFinder.dto.RegisterRequest;
import com.gryszko.eventFinder.exception.*;
import com.gryszko.eventFinder.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) throws TokenException, NotFoundException {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);

    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

}
