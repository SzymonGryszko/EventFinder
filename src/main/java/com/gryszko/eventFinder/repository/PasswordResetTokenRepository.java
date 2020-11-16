package com.gryszko.eventFinder.repository;

import com.gryszko.eventFinder.model.PasswordResetToken;
import com.gryszko.eventFinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String Token);
    void deleteByToken(String token);
}
