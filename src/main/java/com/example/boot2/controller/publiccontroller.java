package com.example.boot2.controller;

import com.example.boot2.cache.AppCache;
import com.example.boot2.entity.Users;
import com.example.boot2.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class publiccontroller {

    @Autowired
    private userService uService;

    @Autowired
    private AppCache appCache;

    @GetMapping("check")
    public String healthchek() {
        return "OK";
    }

    @PostMapping("/create-user")
    public void createuser(@RequestBody Users user) {
        uService.savenewuser(user);
    }

    @GetMapping("clear-app-cache")
    public void clearappcache() {
        appCache.init();
    }
}

//uRepo.delete(uService.findByUserName(auth.getName()));