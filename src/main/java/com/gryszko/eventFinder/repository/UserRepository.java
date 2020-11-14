package com.gryszko.eventFinder.repository;

import com.gryszko.eventFinder.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
