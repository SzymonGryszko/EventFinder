package com.gryszko.eventFinder.service;

import com.gryszko.eventFinder.dto.*;
import com.gryszko.eventFinder.exception.*;
import com.gryszko.eventFinder.model.NotificationEmail;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.model.VerificationToken;
import com.gryszko.eventFinder.repository.UserRepository;

import com.gryszko.eventFinder.repository.VerificationTokenRepository;
import com.gryszko.eventFinder.security.JwtConfig;
import com.gryszko.eventFinder.security.JwtProvider;
import com.gryszko.eventFinder.security.UserRole;
import com.gryszko.eventFinder.utils.PasswordValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final JwtConfig jwtConfig;
    private final RefreshTokenService refreshTokenService;

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
        verificationToken.setExpirationDate(Instant.now().plusSeconds(86400));

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) throws ExpiryException, NotFoundException {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new ExpiryException("Invalid Token"));

        if (verificationToken.get().getExpirationDate().isAfter(Instant.now())) {
            fetchUserAndEnable(verificationToken.get());
            verificationTokenRepository.delete(verificationToken.get());
        } else {
            throw new ExpiryException("Token has expired");
        }

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
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtConfig.getTokenExpirationTime()))
                .username(loginRequest.getUsername())
                .build();
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
        String passwordResetToken = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail("EventFinder password reset",
                user.getEmail(), "Please click the link below to reset your password + " +
                "http://localhost:8080/api/auth/resetPassword/" + passwordResetToken));
    }


    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest, String token) throws PasswordValidationException, NotFoundException, ExpiryException {
        if (!passwordResetRequest.getNewPassword().equals(passwordResetRequest.getConfirmationPassword())) {
            throw new PasswordValidationException("New password is not the same as confirmation password");
        }
        if (!PasswordValidator.validate(passwordResetRequest.getNewPassword())) {
            throw new PasswordValidationException("Password needs to be between 8 and 30 characters," +
                    " contain at least one digit, at least one lower and uppercase letter, no whitespaces allowed");
        }

        VerificationToken passwordResetToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Token not found " + token));

        if (passwordResetToken.getExpirationDate().isAfter(Instant.now())) {
            User user = passwordResetToken.getUser();
            user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
            userRepository.save(user);

            verificationTokenRepository.delete(passwordResetToken);
        } else {
            throw new ExpiryException("Token has expired");
        }

    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) throws ExpiryException, NotFoundException {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());

        User user = userRepository.findByUsername(refreshTokenRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserRole userRole = user.getUserRole();
        String username = user.getUsername();
        String token = jwtProvider.generateTokenWithUsernameAndRole(username, userRole);

        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtConfig.getTokenExpirationTime()))
                .username(username)
                .build();
    }

    public void logout(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() throws UnauthorizedException {
        try{
            org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();

            return userRepository.findByUsername(principal.getUsername()).get();
        } catch (Exception e) {
            throw new UnauthorizedException("You are not authenticated");
        }
    }
}
