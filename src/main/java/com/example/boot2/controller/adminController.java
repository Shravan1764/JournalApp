package com.example.boot2.controller;


import com.example.boot2.entity.Users;
import com.example.boot2.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class adminController {
    @Autowired
    private userService urService;

    @GetMapping("/all-users")
    public ResponseEntity<?> getallusers() {
        List<Users> all = urService.jetall();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin-user")
    public void createnewuser(@RequestBody Users user) {
        urService.savenewadmin(user);
    }
}
