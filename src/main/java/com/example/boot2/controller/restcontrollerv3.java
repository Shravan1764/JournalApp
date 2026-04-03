package com.example.boot2.controller;

import com.example.boot2.entity.Journal;
import com.example.boot2.entity.Users;
import com.example.boot2.service.journalentryservice;
import com.example.boot2.service.userService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class restcontrollerv3 {

    @Autowired
    private journalentryservice jService;

    @Autowired
    private userService uService;

    @GetMapping
    public ResponseEntity<?> getalljentryofuser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Users  usr = uService.findByUserName(username);
        List<Journal> all = usr.getJournalEntries();
        if (all !=null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Journal> createentry(@RequestBody Journal journal) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            jService.save(journal,username);
            return new ResponseEntity<>(journal, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myid}")
    public ResponseEntity<Journal> getentry(@PathVariable ObjectId myid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Users usr = uService.findByUserName(username);
        List<Journal> collect = usr.getJournalEntries().stream().filter(x->x.getId().equals(myid)).collect(Collectors.toList());
        if(!collect.isEmpty()) {
            Optional<Journal> jentry= jService.findentry(myid);
            if(jentry.isPresent()){
                return new ResponseEntity<>(jentry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>( HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("id/{myid}")
    public ResponseEntity<?> deleteentry(@PathVariable ObjectId myid) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean removed = jService.deleteentity(myid, username);
        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("id/{myid}")
    public ResponseEntity<?> updateentry(@RequestBody Journal entry, @PathVariable ObjectId myid){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Users usr = uService.findByUserName(username);
        List<Journal> collect = usr.getJournalEntries().stream().filter(x->x.getId().equals(myid)).collect(Collectors.toList());
        if(!collect.isEmpty()) {
            Optional<Journal> jentry= jService.findentry(myid);
            if(jentry.isPresent()){
                Journal old= jentry.get();
                old.setTitle(entry.getTitle()!=null && !entry.getTitle().equals("") ? entry.getTitle(): old.getTitle());
                old.setDescription(entry.getDescription()!=null && !entry.getDescription().equals("") ? entry.getDescription():old.getDescription());
                old.setDate(entry.getDate());
                jService.save(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}


