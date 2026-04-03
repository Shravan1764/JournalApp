package com.example.boot2.service;

import com.example.boot2.entity.Users; // Assuming 'Users' is your entity class
import com.example.boot2.repository.userRepo; // Renamed from 'userRepo'
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service; // Using @Service for clarity

@Service // Changed from @Component for better semantics
public class UserDetailsServiceImpl implements UserDetailsService {

    private final userRepo userRepository; // Renamed from 'urepository'

    // Constructor Injection
    public UserDetailsServiceImpl(userRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);
        if (user != null) {
            return User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRoles().toArray(new String[0]))
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
