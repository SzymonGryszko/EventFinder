package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.*;
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

    @GetMapping("/remindUsername")
    public ResponseEntity<String> remindUsername(@RequestParam(name = "email", required = true) String email) throws NotFoundException, EmailException {
        authService.remindUsername(email);
        return new ResponseEntity<>("Username reminder email successfully sent", HttpStatus.OK);
    }

    @GetMapping("/resetPassword")
    public ResponseEntity<String> sendResetPasswordEmail(@RequestParam(name = "email", required = true) String email) throws NotFoundException, EmailException {
        authService.sendResetPasswordEmail(email);
        return new ResponseEntity<>("Password reset email successfully sent", HttpStatus.OK);
    }

    @PutMapping("/resetPassword/{token}")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest,
                                                @PathVariable(name = "token", required = true) String token) throws PasswordValidationException, NotFoundException {
        authService.resetPassword(passwordResetRequest, token);
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);

    }

    @PostMapping("/refreshToken")
    public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) throws NotFoundException, TokenException {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Refresh Token Deleted Successfully!!");
    }

}
