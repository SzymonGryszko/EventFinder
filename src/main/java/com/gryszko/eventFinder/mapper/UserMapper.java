package com.gryszko.eventFinder.mapper;

import com.gryszko.eventFinder.dto.UserDto;
import com.gryszko.eventFinder.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }

}
