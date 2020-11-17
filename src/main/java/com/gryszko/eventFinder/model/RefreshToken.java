package com.gryszko.eventFinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

import static javax.persistence.GenerationType.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String token;
    private Instant createdDate;
}
