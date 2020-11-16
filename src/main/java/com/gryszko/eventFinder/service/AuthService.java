package com.gryszko.eventFinder.service;

import com.gryszko.eventFinder.dto.AuthenticationResponse;
import com.gryszko.eventFinder.dto.LoginRequest;
import com.gryszko.eventFinder.dto.PasswordResetRequest;
import com.gryszko.eventFinder.dto.RegisterRequest;
import com.gryszko.eventFinder.exception.*;
import com.gryszko.eventFinder.model.NotificationEmail;
import com.gryszko.eventFinder.model.PasswordResetToken;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.model.VerificationToken;
import com.gryszko.eventFinder.repository.PasswordResetTokenRepository;
import com.gryszko.eventFinder.repository.UserRepository;

import com.gryszko.eventFinder.repository.VerificationTokenRepository;
import com.gryszko.eventFinder.security.JwtProvider;
import com.gryszko.eventFinder.security.UserRole;
import com.gryszko.eventFinder.validators.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public void signup(RegisterRequest registerRequest) throws EntityAlreadyExistsException, PasswordValidationException, EmailException {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new EntityAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EntityAlreadyExistsException("Email already exists");
        }
        if (!PasswordValidator.validate(registerRequest.getPassword())) {
            throw new PasswordValidationException("Password needs to be between 8 and 30 characters," +
                    " contain at least one digit, at least one lower and uppercase letter, no whitespaces allowed");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        user.setUserRole(UserRole.USER);
        user.setEvents(new HashSet<>());

        userRepository.save(user);

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail("Please activate your account",
                user.getEmail(), "Please click the link below to activate your account + " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) throws TokenException, NotFoundException {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new TokenException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) throws NotFoundException {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new AuthenticationResponse(token, loginRequest.getUsername());
    }

    public void remindUsername(String email) throws NotFoundException, EmailException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No username found for given email"));
        mailService.sendMail(new NotificationEmail("Username reminder for EventFinder",
                email,
                "Your username is " + user.getUsername()));
    }


    @Transactional
    public void sendResetPasswordEmail(String email) throws NotFoundException, EmailException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("No username found for given email"));
        String passwordResetToken = generatePasswordResetToken(user);

        mailService.sendMail(new NotificationEmail("EventFinder password reset",
                user.getEmail(), "Please click the link below to reset your password + " +
                "http://localhost:8080/api/auth/resetPassword/" + passwordResetToken));
    }

    private String generatePasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);

        passwordResetTokenRepository.save(passwordResetToken);
        return token;
    }

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest, String token) throws PasswordValidationException, NotFoundException {
        if (!passwordResetRequest.getNewPassword().equals(passwordResetRequest.getConfirmationPassword())) {
            throw new PasswordValidationException("New password is not the same as confirmation password");
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Token not found " + token));
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);

    }
}
