package com.gryszko.eventFinder;

import com.gryszko.eventFinder.model.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackageClasses = {
		Comment.class,
		Event.class,
		User.class,
		VerificationToken.class
})
@EnableAsync
public class EventFinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventFinderApplication.class, args);
	}

}
