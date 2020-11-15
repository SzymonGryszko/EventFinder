package com.gryszko.eventFinder.repository;

import com.gryszko.eventFinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
