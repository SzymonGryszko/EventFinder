package com.gryszko.eventFinder.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationToken extends CrudRepository<com.gryszko.eventFinder.model.VerificationToken, Long> {
}
