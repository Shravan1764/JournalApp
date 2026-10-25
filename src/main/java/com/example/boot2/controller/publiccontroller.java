package com.example.boot2.controller;

import com.example.boot2.cache.AppCache;
import com.example.boot2.entity.Users;
import com.example.boot2.service.UserDetailsServiceImpl;
import com.example.boot2.service.userService;
import com.example.boot2.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@Slf4j
public class publiccontroller {

    @Autowired
    private userService uService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppCache appCache;

    @GetMapping("check")
    public String healthchek() {
        return "OK";
    }

    @PostMapping("/signup")
    public void signup(@RequestBody Users user) {
        uService.savenewuser(user);
    }
    // FOR EVERY REQUEST NOW TAHT you have logged in the bearer token is now generated so now for all the
    // requests you have to only send the bearer token not the usename and password for every request
    //bearer means who so ever has that token can access the resources

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Users user) {
        log.info("i am here");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getUsername());
            String jwt  = jwtUtil.generateToken(userDetails.getUsername());
            return  new ResponseEntity<>(jwt,HttpStatus.OK);
        }catch (Exception e) {
            log.error("Exception occured while create auth token",e);
            return new ResponseEntity<>("Incorrect username and password",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("clear-app-cache")
    public void clearappcache() {
        appCache.init();
    }
}

//uRepo.delete(uService.findByUserName(auth.getName()));