package com.example.boot2.controller;

import com.example.boot2.entity.Users;
import com.example.boot2.repository.userRepo;
import com.example.boot2.scheduler.UserScheduler;
import com.example.boot2.service.userService;
import com.example.boot2.service.weatherservice;
import com.journal.response.pojo.weatherpojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class userController {
    @Autowired
    private userService uService;
    @Autowired
    private userRepo uRepo;

    @Autowired
    private weatherservice weatherservice;


    @Autowired
    private UserScheduler userScheduler;


    @GetMapping("/criteria")
    public void getallusers() {
        userScheduler.fetchuserdetailstosendmail();
    }

    @PutMapping
    public ResponseEntity<?> updateuser(@RequestBody Users user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Users userinDB = uService.findByUserName(username);
            userinDB.setUsername(user.getUsername());
            userinDB.setPassword(user.getPassword());
            userinDB.setSentimentAnalysis(user.getSentimentAnalysis());
            userinDB.setEmail(user.getEmail());
            uService.savenewuser(userinDB);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping
    public ResponseEntity<?> deleteuser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        uRepo.deleteByUsername(auth.getName());
        //uRepo.delete(uService.findByUserName(au   th.getName()));
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> greetings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        weatherpojo wresponse = weatherservice.getCurrentWeather("Mumbai");
        String greeting = "";
        if(wresponse != null) {
            greeting = "  , weather feels like : " + wresponse.getCurrent().getFeelslike();
        }
        return  new ResponseEntity<>("Hii"+auth.getName() + greeting,HttpStatus.OK);
    }
}
// with the help of link of this page you will create the username and password
// on the restcontroller page you will search the username or use the username and create the new journal
