package com.gryszko.eventFinder.service;

import com.gryszko.eventFinder.dto.RegisterRequest;
import com.gryszko.eventFinder.exception.EmailException;
import com.gryszko.eventFinder.exception.EntityAlreadyExistsException;
import com.gryszko.eventFinder.exception.PasswordValidationException;
import com.gryszko.eventFinder.model.NotificationEmail;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.model.VerificationToken;
import com.gryszko.eventFinder.repository.UserRepository;

import com.gryszko.eventFinder.repository.VerificationTokenRepository;
import com.gryszko.eventFinder.security.UserRole;
import com.gryszko.eventFinder.validators.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                "http://localhost:8080/api/auth.accountVerification/" + token));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }
}
