package com.example.boot2.service;

import com.example.boot2.entity.Journal;
import com.example.boot2.entity.Users;
import com.example.boot2.repository.enitityrepository;
import com.example.boot2.repository.userRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class userService {

   //private static final Logger logger = LoggerFactory.getLogger(journalentryservice.class);
    // like for getterand setter and lombok we can use @slf4j and can comment this line

    @Autowired
    private userRepo uRepo;

    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void save(Users user) {
        uRepo.save(user);
    }

    public boolean savenewuser(Users user) {
        try {
            Users existing = uRepo.findByUsername(user.getUsername());

            if (existing != null) {
                existing.setPassword(passwordEncoder.encode(user.getPassword()));
                existing.setRoles(List.of("USER"));
                existing.setEmail(user.getEmail());
                existing.setSentimentAnalysis(user.getSentimentAnalysis());
                uRepo.save(existing);
                return true;
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(List.of( "USER"));
            uRepo.save(user);

        } catch (Exception e) {
            System.err.println("Error while creating/updating admin user: " + e.getMessage());
            throw new RuntimeException("Failed to save admin user", e);
        }
        return true;
    }
    public void savenewadmin(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("ADMIN", "USER"));
        user.setSentimentAnalysis(user.getSentimentAnalysis());
        user.setEmail(user.getEmail());
        uRepo.save(user);
    }
    public  List<Users> jetall() {
        return uRepo.findAll();
    }
    //Optional can be considered as a box either it can contain data or not
    public Optional<Users> findentry(@PathVariable ObjectId myid) {
        return uRepo.findById(myid);
    }

    public void deleteentity(@PathVariable ObjectId myid) {
        uRepo.deleteById(myid);
    }


    public Users findByUserName(String username) {
        return uRepo.findByUsername(username);
    }
}
