package com.gryszko.eventFinder.service;

import com.gryszko.eventFinder.dto.UserDto;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.mapper.UserMapper;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.repository.UserRepository;
import com.gryszko.eventFinder.security.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.gryszko.eventFinder.security.UserRole.*;

@AllArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public void updateUserRole(Long id, Integer role) throws NotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        switch (role) {
            case 0:
                user.setUserRole(USER);
                break;
            case 1:
                user.setUserRole(ORGANIZER);
                break;
            case 2:
                user.setUserRole(ADMIN);
        }
        userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapUserToDto)
                .collect(Collectors.toList());
    }
}
