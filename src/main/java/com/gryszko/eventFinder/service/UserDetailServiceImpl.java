package com.gryszko.eventFinder.service;

import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.model.User;
import com.gryszko.eventFinder.repository.UserRepository;
import com.gryszko.eventFinder.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new NotFoundException("User not found " + username));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isEnabled(), true, true, true, getAuthorities(user.getUserRole()));

    }

    private Collection<? extends GrantedAuthority> getAuthorities(UserRole userRole) {
        return singletonList(new SimpleGrantedAuthority(userRole.toString()));
    }
}
