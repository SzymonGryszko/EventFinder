package com.gryszko.eventFinder.model;

import com.gryszko.eventFinder.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "eventfinder_users")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    @Email
    @NotBlank(message = "Email is required")
    private String email;
    private Instant created;
    private boolean isEnabled;
    private UserRole userRole;
    @ManyToMany(fetch = LAZY)
    private Set<Event> events;

}
